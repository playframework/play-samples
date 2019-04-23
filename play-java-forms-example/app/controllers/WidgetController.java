package controllers;

import models.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static play.libs.Scala.asScala;

/**
 * An example of form processing.
 *
 * https://playframework.com/documentation/latest/JavaForms
 */
@Singleton
public class WidgetController extends Controller {

    private final Form<WidgetData> form;
    private final List<Widget> widgets;

    @Inject
    public WidgetController(FormFactory formFactory) {
        this.form = formFactory.form(WidgetData.class);
        this.widgets = com.google.common.collect.Lists.newArrayList(
                new Widget("Data 1", 123),
                new Widget("Data 2", 456),
                new Widget("Data 3", 789)
        );
    }

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result listWidgets() {
        return ok(views.html.listWidgets.render(asScala(widgets), form));
    }

    public Result createWidget() {
        final Form<WidgetData> boundForm = form.bindFromRequest();

        if (boundForm.hasErrors()) {
            play.Logger.ALogger logger = play.Logger.of(getClass());
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.listWidgets.render(asScala(widgets), boundForm));
        } else {
            WidgetData data = boundForm.get();
            widgets.add(new Widget(data.getName(), data.getPrice()));
            flash("info", "Widget added!");
            return redirect(routes.WidgetController.listWidgets());
        }
    }
}
