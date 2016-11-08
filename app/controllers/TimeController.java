package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dagger.Lazy;
import play.data.Form;
import play.data.FormFactory;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletionStage;

public class TimeController extends Controller {

    private final Clock clock;
    private final WSClient ws;
    private final Lazy<FormFactory> formFactory;

    @Inject
    public TimeController(Clock clock, WSClient ws, Lazy<FormFactory> formFactory) {
        this.clock = clock;
        this.ws = ws;
        this.formFactory = formFactory;
    }

    public Result index() {
        final Form<TimeZoneData> form = formFactory.get().form(TimeZoneData.class);

        String timezone = session("timezone");
        Form<TimeZoneData> filledForm;
        if (timezone == null) {
            timezone = TimeZone.getDefault().getID();
            filledForm = form.fill(new TimeZoneData(timezone));
        } else {
            filledForm = form;
        }
        List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());
        return ok(views.html.index.render(filledForm, renderTime(), timezones));
    }

    public Result indexPost() {
        final Form<TimeZoneData> form = formFactory.get().form(TimeZoneData.class);

        final Form<TimeZoneData> boundForm = form.bindFromRequest();
        String[] timezones = TimeZone.getAvailableIDs();
        if (boundForm.hasErrors()) {
            return badRequest(views.html.index.render(boundForm, renderTime(), Arrays.asList(timezones)));
        } else {
            TimeZoneData tzData = boundForm.get();
            session("timezone", tzData.getTimeZone());
            return redirect(routes.TimeController.index());
        }
    }

    public CompletionStage<Result> ws() {
        String url = "http://www.timeapi.org/utc/now.json";
        final String timezone = session("timezone");
        return ws.url(url).get().thenApply(result -> {
            final JsonNode jsonNode = result.asJson();
            final String dateString = jsonNode.findValue("dateString").asText();
            final LocalDateTime ldt = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
            final ZoneId zoneId = zoneId(timezone);
            final ZonedDateTime zdt = ldt.atZone(zoneId);
            final String formatted = formattedDate(zdt);
            return ok("Hello!  The time is " + formatted + " in time zone " + zoneId);
        });
    }

    String renderTime() {
        final String timezone = session("timezone");
        final ZoneId zoneId = zoneId(timezone);
        final Instant instant = clock.instant();
        final ZonedDateTime zdt = instant.atZone(zoneId);
        final String formatted = formattedDate(zdt);
        return formatted;
    }

    ZoneId zoneId(String timezone) {
        ZoneId zoneId;
        if (timezone == null) {
            zoneId = ZoneId.systemDefault();
        } else {
            zoneId = ZoneId.of(timezone);
        }
        return zoneId;
    }

    String formattedDate(ZonedDateTime zdt) {
        return zdt.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
