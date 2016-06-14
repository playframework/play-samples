package controllers;

import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import play.core.j.JavaParsers;
import play.core.parsers.Multipart;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import scala.Function1;
import scala.Option;
import scala.runtime.AbstractFunction1;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

/**
 * This class is a custom body parser that farms out most of the work of
 * multipart form processing to the underlying core parser, but specifies
 * that form data should be treated as type "File" instead of the default
 * "TemporaryFile".
 */
@SuppressWarnings("WeakerAccess")
class MyMultipartFormDataBodyParser implements BodyParser<Http.MultipartFormData<File>> {
    private final play.api.mvc.BodyParser<play.api.mvc.MultipartFormData<File>> delegate;

    private final Materializer materializer;
    private final int maxLength;

    @Inject
    public MyMultipartFormDataBodyParser(Materializer materializer, play.api.http.HttpConfiguration config) {
        this.maxLength = (int) config.parser().maxDiskBuffer();
        this.materializer = materializer;
        this.delegate = multipartFormDataBodyParser();
    }

    /**
     * Delegates underlying functionality to another body parser and converts the
     * result to Java API.
     */
    @Override
    public Accumulator<ByteString, F.Either<Result, Http.MultipartFormData<File>>> apply(Http.RequestHeader request) {
        return delegate.apply(request._underlyingHeader())
                .asJava()
                .map(result -> {
                            if (result.isLeft()) {
                                return F.Either.Left(result.left().get().asJava());
                            } else {
                                final play.api.mvc.MultipartFormData<File> scalaData = result.right().get();
                                return F.Either.Right(new MyFileMultipartFormData(scalaData));
                            }
                        },
                        JavaParsers.trampoline()
                );
    }

    /**
     * Generates a temp file directly without going through TemporaryFile.
     */
    private File generateTempFile() {
        try {
            final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(EnumSet.of(OWNER_READ, OWNER_WRITE));
            final Path path = Files.createTempFile("multipartBody", "tempFile", attr);
            return path.toFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns a BodyParser using a custom Accumulator from an Akka Streams sink that
     * takes FileIO from ByteString and puts it into a FilePart with File.
     */
    private play.api.mvc.BodyParser<play.api.mvc.MultipartFormData<File>> multipartFormDataBodyParser() {
        return Multipart.multipartParser(maxLength, asScalaFunction(fileInfo -> {
            final String filename = fileInfo.fileName();
            final String partname = fileInfo.partName();
            final Option<String> contentType = fileInfo.contentType();

            final File file = generateTempFile();

            final play.api.mvc.MultipartFormData.FilePart<File> part = new play.api.mvc.MultipartFormData.FilePart<>(partname, filename, contentType, file);

            final Sink<ByteString, CompletionStage<IOResult>> sink = FileIO.toFile(file);
            return Accumulator.fromSink(
                    sink.mapMaterializedValue(completionStage ->
                            completionStage.thenApplyAsync(results -> part)
                    )).asScala();
        }), materializer);
    }

    /**
     * Utility function to convert a java.util.Function into a scala.Function1.
     */
    private <T1, R> Function1<T1, R> asScalaFunction(Function<T1, R> function) {
        // This is needed because Multipart.multipartParser takes a Function1 directly,
        // and there is no equivalent Java wrapper.
        return new AbstractFunction1<T1, R>() {
            @Override
            public R apply(T1 t1) {
                return function.apply(t1);
            }
        };
    }

}



