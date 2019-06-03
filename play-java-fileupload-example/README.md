# Play File Upload using a custom BodyParser

This is a sample project that shows how to upload a file through Akka Streams using a custom BodyParser using Akka Streams using the Java API.

## Default MultipartFormData Body Parser

Play's Java API specifies a BodyParser.MultipartFormData class which uses a TemporaryFile wrapper class that creates a file under a "temporary" name and then deletes it only when the system is under GC pressure.

```java
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

```java
@BodyParser.Of(BodyParser.MultipartFormData.class)
```

with:

```java
@BodyParser.Of(MyMultipartFormDataBodyParser.class)
```

And we want to change as little code as possible.  The underlying mechanics are simple.  `MyMultipartFormDataBodyParser` does all the work of setting up a custom file part handler using a method called `createFilePartHandler`:

```java
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
            final Path path = Files.createTempFile("multipartBody", "tempFile");
            return path.toFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
```

The core Accumulator is generated from an `akka.streams.FileIO` sink which writes out bytes to the filesystem, and exposes a CompletionStage when the write operation has been completed.

Because this code delegates to the Scala API implementation, the underlying `DelegatingMultipartFormDataBodyParser<A>` exposes an abstract method:

```java
abstract Function<Multipart.FileInfo, Accumulator<ByteString, Http.MultipartFormData.FilePart<A>>> createFilePartHandler();
```

`DelegatingMultipartFormDataBodyParser` does not know about any particular type, only `FilePart<A>`, and so it falls to the implementation to fill in the details. 
