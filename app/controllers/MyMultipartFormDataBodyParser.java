package controllers;

import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import play.api.http.HttpErrorHandler;
import play.core.parsers.Multipart;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * This class is a custom body parser with a custom file part handler
 * that uses a file that can come from anywhere in the system.
 */
class MyMultipartFormDataBodyParser extends BodyParser.DelegatingMultipartFormDataBodyParser<File> {

    @Inject
    public MyMultipartFormDataBodyParser(Materializer materializer, play.api.http.HttpConfiguration config, HttpErrorHandler errorHandler) {
        super(materializer, config.parser().maxDiskBuffer(), errorHandler);
    }

    /**
     * Creates a file part handler that uses a custom accumulator.
     */
    @Override
    public Function<Multipart.FileInfo, Accumulator<ByteString, Http.MultipartFormData.FilePart<File>>> createFilePartHandler() {
        return this::apply;
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

    private Accumulator<ByteString, Http.MultipartFormData.FilePart<File>> apply(Multipart.FileInfo fileInfo) {
        final String filename = fileInfo.fileName();
        final String partname = fileInfo.partName();
        final String contentType = fileInfo.contentType().getOrElse(null);
        final File file = generateTempFile();

        final Sink<ByteString, CompletionStage<IOResult>> sink = FileIO.toFile(file);
        return Accumulator.fromSink(
                sink.mapMaterializedValue(completionStage ->
                        completionStage.thenApplyAsync(results -> {
                            //noinspection unchecked
                            return new Http.MultipartFormData.FilePart<>(partname,
                                    filename,
                                    contentType,
                                    file);
                        })
                ));
    }
}



