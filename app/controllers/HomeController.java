package controllers;

import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class uses a custom body parser to change the upload type.
 */
@Singleton
public class HomeController extends Controller {

    private final play.data.FormFactory formFactory;

    @Inject
    public HomeController(play.data.FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        Form<FormData> form = formFactory.form(FormData.class);
        play.mvc.Http.Context context = play.mvc.Http.Context.current();
        return ok(index.render(form, context.messages()));
    }

    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result upload() throws IOException {
        final Http.Context context = Http.Context.current();
        final Http.Request request = context.request();

        final Http.MultipartFormData.FilePart<Object> filePart = request.body().asMultipartFormData().getFile("name");
        final File file = (File) filePart.getFile();
        final long data = operateOnTempFile(file);
        return ok("file size = " + data + "");
    }

    private long operateOnTempFile(File file) throws IOException {
        final long size = Files.size(file.toPath());
        Files.deleteIfExists(file.toPath());
        return size;
    }

}

