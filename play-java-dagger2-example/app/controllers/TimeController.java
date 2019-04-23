package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dagger.Lazy;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
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
    private final Form<TimeZoneData> form ;

    @Inject
    public TimeController(Clock clock, WSClient ws, FormFactory formFactory) {
        this.clock = clock;
        this.ws = ws;
        this.form = formFactory.form(TimeZoneData.class);
    }

    public Result index() {
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

    public Result now() {
        String date = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        ObjectNode dateObj = Json.newObject().put("dateString", date);
        return ok(Json.toJson(dateObj));
    }

    // call out to local URL as if it's a remote REST API, since timeapi is down
    public CompletionStage<Result> ws() {
        String url = "http://localhost:9000/now";
        final String timezone = session("timezone");
        return ws.url(url).get().thenApply(result -> {
            final JsonNode jsonNode = result.asJson();
            final String dateString = jsonNode.findValue("dateString").asText();
            final Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateString));
            final ZoneId zoneId = zoneId(timezone);
            final ZonedDateTime zdt = instant.atZone(zoneId);
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
