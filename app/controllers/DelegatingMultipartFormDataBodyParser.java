package controllers;


import akka.stream.Materializer;
import akka.util.ByteString;
import play.core.j.JavaParsers;
import play.core.parsers.Multipart;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import scala.Function1;
import scala.Option;
import scala.collection.Seq;
import scala.compat.java8.OptionConverters;
import scala.runtime.AbstractFunction1;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static scala.collection.JavaConverters.mapAsJavaMapConverter;
import static scala.collection.JavaConverters.seqAsJavaListConverter;

/**
 * An abstract body parser that exposes a file part handler as an
 * abstract method and delegates the implementation to the underlying
 * Scala multipartParser.
 */
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
