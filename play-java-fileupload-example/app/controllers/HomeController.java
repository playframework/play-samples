package controllers;

import play.data.Form;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.*;
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
    private MessagesApi messagesApi;

    @Inject
    public HomeController(play.data.FormFactory formFactory, MessagesApi messagesApi) {
        this.formFactory = formFactory;
        this.messagesApi = messagesApi;
    }

    public Result index(Http.Request request ) {
        Form<FormData> form = formFactory.form(FormData.class).bindFromRequest(request);
        Messages messages = this.messagesApi.preferred(request);
        return ok(index.render(form, request, messages));
    }

    /**
     * This method uses MyMultipartFormDataBodyParser as the body parser
     */
    @BodyParser.Of(MyMultipartFormDataBodyParser.class)
    public Result upload() throws IOException {
        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("name");
        final File file = filePart.getFile();
        final long data = operateOnTempFile(file);
        return ok("file size = " + data + "");
    }

    private long operateOnTempFile(File file) throws IOException {
        final long size = Files.size(file.toPath());
        Files.deleteIfExists(file.toPath());
        return size;
    }

}

