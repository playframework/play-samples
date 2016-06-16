# Play File Upload using a custom BodyParser

This is a sample project that shows how to upload a file through Akka Streams using a custom BodyParser using Akka Streams using the Java API.

## Default MultipartFormData Body Parser
 
Play's Java API specifies a BodyParser.MultipartFormData class which uses a TemporaryFile wrapper class that creates a file under a "temporary" name and then deletes it only when the system is under GC pressure.
     
``` java
@BodyParser.Of(BodyParser.MultipartFormData.class)
public Result upload() throws IOException {
    final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
    final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("name");
    final File file = filePart.getFile();
    final long data = operateOnTempFile(file);
    return ok("file size = " + data + "");
}
```

## Customizing the Body Parser

There are cases where it's useful to have more control over where and Play uploads multi part form data.  In this case, we'd like to get access to the accumulated byte stream for each file part and generate a file directly, without going through TemporaryFile.

In short, we want to replace:

```
@BodyParser.Of(BodyParser.MultipartFormData.class)
```

with 

```
@BodyParser.Of(MyMultipartFormDataBodyParser.class)
```

And we want to change as little code as possible.  The underlying mechanics are simple.  `MyMultipartFormDataBodyParser` does all the work of setting up a custom file part handler using a method called `createFilePartHandler`:

``` java
class MyMultipartFormDataBodyParser extends DelegatingMultipartFormDataBodyParser<File> {

    @Inject
    public MyMultipartFormDataBodyParser(Materializer materializer, play.api.http.HttpConfiguration config) {
        super(materializer, config.parser().maxDiskBuffer());
    }

    /**
     * Creates a file part handler that uses a custom accumulator.
     */
    @Override
    public Function<Multipart.FileInfo, Accumulator<ByteString, Http.MultipartFormData.FilePart<File>>> createFilePartHandler() {
        return (Multipart.FileInfo fileInfo) -> {
            final String filename = fileInfo.fileName();
            final String partname = fileInfo.partName();
            final String contentType = fileInfo.contentType().getOrElse(null);
            final File file = generateTempFile();

            final Sink<ByteString, CompletionStage<IOResult>> sink = FileIO.toFile(file);
            return Accumulator.fromSink(
                    sink.mapMaterializedValue(completionStage ->
                            completionStage.thenApplyAsync(results -> {
                                //noinspection unchecked
                                return new Http.MultipartFormData.FilePart(partname,
                                        filename,
                                        contentType,
                                        file);
                            })
                    ));
        };
    }

    /**
     * Generates a temp file directly without going through TemporaryFile.
     */
    private File generateTempFile() {
        try {
            final EnumSet<PosixFilePermission> attrs = EnumSet.of(OWNER_READ, OWNER_WRITE);
            final FileAttribute<?> attr = PosixFilePermissions.asFileAttribute(attrs);
            final Path path = Files.createTempFile("multipartBody", "tempFile", attr);
            return path.toFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
```

The core Accumulator is generated from an `akka.streams.FileIO` sink which writes out bytes to the filesystem, and exposes a CompletionStage when the write operation has been completed.

Because this code delegates to the Scala API implementation, the underlying `DelegatingMultipartFormDataBodyParser<A>` exposes an abstract method:
 
``` java
abstract Function<Multipart.FileInfo, Accumulator<ByteString, Http.MultipartFormData.FilePart<A>>> createFilePartHandler();
```

`DelegatingMultipartFormDataBodyParser` does not know about any particular type, only `FilePart<A>`, and so it falls to the implementation to fill in the details. 
 
In addition, `DelegatingMultipartFormDataBodyParser` does all the housekeeping necessary to map between Java and the underlying details.  This code is verbose, but illustrates that pure Java can do all the work natively of working with Scala, rather than having to add a Scala class:  
 
``` java
abstract class DelegatingMultipartFormDataBodyParser<A> implements BodyParser<Http.MultipartFormData<A>> {

    private final Materializer materializer;
    private final long maxLength;
    private final play.api.mvc.BodyParser<play.api.mvc.MultipartFormData<A>> delegate;

    public DelegatingMultipartFormDataBodyParser(Materializer materializer, long maxLength) {
        this.maxLength = maxLength;
        this.materializer = materializer;
        delegate = multipartParser();
    }

    /**
     * Returns a FilePartHandler expressed as a Java function.
     */
    abstract Function<Multipart.FileInfo, play.libs.streams.Accumulator<ByteString, Http.MultipartFormData.FilePart<A>>> createFilePartHandler();

    /**
     * Calls out to the Scala API to create a multipart parser.
     */
    private play.api.mvc.BodyParser<play.api.mvc.MultipartFormData<A>> multipartParser() {
        ScalaFilePartHandler filePartHandler = new ScalaFilePartHandler();
        //noinspection unchecked
        return Multipart.multipartParser((int) maxLength, filePartHandler, materializer);
    }

    private class ScalaFilePartHandler extends AbstractFunction1<Multipart.FileInfo, play.api.libs.streams.Accumulator<ByteString, play.api.mvc.MultipartFormData.FilePart<A>>> {
            @Override
            public play.api.libs.streams.Accumulator<ByteString, play.api.mvc.MultipartFormData.FilePart<A>> apply(Multipart.FileInfo fileInfo) {
                return createFilePartHandler()
                        .apply(fileInfo)
                        .asScala()
                        .map(new JavaFilePartToScalaFilePart(), materializer.executionContext());
            }
    }

    private class JavaFilePartToScalaFilePart extends AbstractFunction1<Http.MultipartFormData.FilePart<A>, play.api.mvc.MultipartFormData.FilePart<A>> {
        @Override
        public play.api.mvc.MultipartFormData.FilePart<A> apply(Http.MultipartFormData.FilePart<A> filePart) {
            return toScala(filePart);
        }
    }

    /**
     * Delegates underlying functionality to another body parser and converts the
     * result to Java API.
     */
    @Override
    public play.libs.streams.Accumulator<ByteString, F.Either<Result, Http.MultipartFormData<A>>> apply(Http.RequestHeader request) {
        return delegate.apply(request._underlyingHeader())
                .asJava()
                .map(result -> {
                            if (result.isLeft()) {
                                return F.Either.Left(result.left().get().asJava());
                            } else {
                                final play.api.mvc.MultipartFormData<A> scalaData = result.right().get();
                                return F.Either.Right(new DelegatingMultipartFormData(scalaData));
                            }
                        },
                        JavaParsers.trampoline()
                );
    }


    /**
     * Extends Http.MultipartFormData to use File specifically,
     * converting from Scala API to Java API.
     */
    private class DelegatingMultipartFormData extends Http.MultipartFormData<A> {

        private play.api.mvc.MultipartFormData<A> scalaFormData;

        DelegatingMultipartFormData(play.api.mvc.MultipartFormData<A> scalaFormData) {
            this.scalaFormData = scalaFormData;
        }

        @Override
        public Map<String, String[]> asFormUrlEncoded() {
            return mapAsJavaMapConverter(
                    scalaFormData.asFormUrlEncoded().mapValues(arrayFunction())
            ).asJava();
        }

        // maps from Scala Seq to String array
        private Function1<Seq<String>, String[]> arrayFunction() {
            return new AbstractFunction1<Seq<String>, String[]>() {
                @Override
                public String[] apply(Seq<String> v1) {
                    String[] array = new String[v1.size()];
                    v1.copyToArray(array);
                    return array;
                }
            };
        }

        @Override
        public List<FilePart<A>> getFiles() {
            return seqAsJavaListConverter(scalaFormData.files())
                    .asJava()
                    .stream()
                    .map(part -> toJava(part))
                    .collect(Collectors.toList());
        }

    }

    private Http.MultipartFormData.FilePart<A> toJava(play.api.mvc.MultipartFormData.FilePart<A> filePart) {
        return new Http.MultipartFormData.FilePart<>(
                filePart.key(),
                filePart.filename(),
                OptionConverters.toJava(filePart.contentType()).orElse(null),
                filePart.ref()
        );
    }

    private play.api.mvc.MultipartFormData.FilePart<A> toScala(Http.MultipartFormData.FilePart<A> filePart) {
        return new play.api.mvc.MultipartFormData.FilePart<>(
                filePart.getKey(),
                filePart.getFilename(),
                Option.apply(filePart.getContentType()),
                filePart.getFile()
        );
    }
}
```

