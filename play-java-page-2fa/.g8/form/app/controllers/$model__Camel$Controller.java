package controllers;

import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

// Add the following to conf/routes 
/*
GET     /$model;format="camel"$        controllers.$model;format="Camel"$Controller.$model;format="camel"$Get(request: Request)
POST    /$model;format="camel"$        controllers.$model;format="Camel"$Controller.$model;format="camel"$Post(request: Request)
*/

/**
 * $model;format="Camel"$ form controller for Play Java
 */
public class $model;format="Camel"$Controller extends Controller {

    private final Form<$model;format="Camel"$Data> $model;format="camel"$Form;
    private final MessagesApi messagesApi;

    @Inject
    public $model;format="Camel"$Controller(FormFactory formFactory, MessagesApi messagesApi) {
        this.$model;format="camel"$Form = formFactory.form($model;format="Camel"$Data.class);
        this.messagesApi = messagesApi;
    }

    public Result $model;format="camel"$Get(Http.Request request) {
        return ok(views.html.$model;format="camel"$.form.render($model;format="camel"$Form, request, messagesApi.preferred(request)));
    }

    public Result $model;format="camel"$Post(Http.Request request) {
        Form<$model;format="Camel"$Data> boundForm = $model;format="camel"$Form.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            return badRequest(views.html.$model;format="camel"$.form.render(boundForm, request, messagesApi.preferred(request)));
        } else {
            $model;format="Camel"$Data $model;format="camel"$ = boundForm.get();
            return redirect(routes.$model;format="Camel"$Controller.$model;format="camel"$Get()).flashing("success", "$model;format="Camel"$ " + $model;format="camel"$);
        }
    }

}
