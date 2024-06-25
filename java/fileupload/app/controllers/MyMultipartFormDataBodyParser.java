package controllers;

import org.apache.pekko.stream.IOResult;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.stream.javadsl.FileIO;
import org.apache.pekko.stream.javadsl.Sink;
import org.apache.pekko.util.ByteString;
import play.core.parsers.Multipart;
import play.http.HttpErrorHandler;
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
        super(materializer, config.parser().maxMemoryBuffer(), config.parser().maxDiskBuffer(), false, errorHandler);
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

        final Sink<ByteString, CompletionStage<IOResult>> sink = FileIO.toPath(file.toPath());
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
