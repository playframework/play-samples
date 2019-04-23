package controllers;

import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.junit.Test;
import play.Application;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.write;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static play.test.Helpers.*;

public class HomeControllerTest {

    @Test
    public void testFileUpload() {
        Application app = fakeApplication();
        running(app, () -> {
            try {

                Files.TemporaryFileCreator temporaryFileCreator = app.injector().instanceOf(Files.TemporaryFileCreator.class);
                Materializer materializer = app.injector().instanceOf(Materializer.class);

                Path tempfilePath = createTempFile(null, "tempfile");
                write(tempfilePath, "My string to save".getBytes("utf-8"));

                Source<ByteString, CompletionStage<IOResult>> source = FileIO.fromPath(tempfilePath);
                Http.MultipartFormData.FilePart<Source<ByteString, ?>> part = new Http.MultipartFormData.FilePart<>("name", "filename", "text/plain", source);
                Http.RequestBuilder request = fakeRequest()
                        .method(POST)
                        .bodyMultipart(singletonList(part), temporaryFileCreator, materializer)
                        .uri("/upload");

                Result result = route(app, request);
                String actual = contentAsString(result);
                assertEquals("file size = 17", actual);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

}
