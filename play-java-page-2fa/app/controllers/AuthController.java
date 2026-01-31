package controllers;

import play.mvc.*;
import play.data.FormFactory;
import play.data.Form;
import play.i18n.MessagesApi; // <-- 1. Add correct import for MessagesApi
import services.TotpService;

import javax.inject.Inject;

public class AuthController extends Controller {

    private final Form<VerificationCode> form;
    private final TotpService totpService;
    private final MessagesApi messagesApi; // <-- 2. Declare the messagesApi field

    private static final String AUTH_SESSION_KEY = "verified";

    @Inject
    // 3. Inject MessagesApi in the constructor
    public AuthController(FormFactory formFactory, TotpService totpService, MessagesApi messagesApi) {
        this.form = formFactory.form(VerificationCode.class);
        this.totpService = totpService;
        this.messagesApi = messagesApi; // <-- 4. Assign it
    }

    public Result protectedContent(Http.Request request) {
        if (!request.session().get(AUTH_SESSION_KEY).map(val -> val.equals("true")).orElse(false)) {
            return redirect(routes.AuthController.verifyPage()).flashing("warning", "Please verify to access this page.");
        }
        return ok(views.html.protectedContent.render());
    }

    public Result verifyPage(Http.Request request) {
        // All calls that use messagesApi will now work
        return ok(views.html.verificationForm.render(form, request, messagesApi.preferred(request)));
    }

    public Result verify(Http.Request request) {
        final Form<VerificationCode> boundForm = form.bindFromRequest(request);

        if (boundForm.hasErrors()) {
            return badRequest(views.html.verificationForm.render(boundForm, request, messagesApi.preferred(request)));
        }

        String code = boundForm.get().getCode();
        if (totpService.verifyCode(code)) {
            return redirect(routes.AuthController.protectedContent()).addingToSession(request, AUTH_SESSION_KEY, "true");
        } else {
            return badRequest(views.html.verificationForm.render(boundForm.withError("code", "Invalid code."), request, messagesApi.preferred(request)));
        }
    }

    public static class VerificationCode {
        private String code;
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }
}