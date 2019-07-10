package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dagger.Lazy;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.CompletionStage;

public class TimeController extends Controller {

    private final Clock clock;
    private final WSClient ws;
    private final Form<TimeZoneData> form;
    private MessagesApi messagesApi;

    @Inject
    public TimeController(Clock clock, WSClient ws, FormFactory formFactory, MessagesApi messagesApi) {
        this.clock = clock;
        this.ws = ws;
        this.form = formFactory.form(TimeZoneData.class);
        this.messagesApi = messagesApi;
    }

    public Result index(Http.Request request) {
        Optional<String> timezone = request.session().get("timezone");
        Form<TimeZoneData> filledForm;
        if (timezone.isPresent()) {
            filledForm = form;
        } else {
            String tz = TimeZone.getDefault().getID();
            filledForm = form.fill(new TimeZoneData(tz));
        }
        List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());
        return ok(views.html.index.render(filledForm, renderTime(request), timezones, request, messagesApi.preferred(request)));
    }

    public Result indexPost(Http.Request request) {
        final Form<TimeZoneData> boundForm = form.bindFromRequest(request);
        String[] timezones = TimeZone.getAvailableIDs();
        if (boundForm.hasErrors()) {
            return badRequest(views.html.index.render(boundForm, renderTime(request), Arrays.asList(timezones), request, messagesApi.preferred(request)));
        } else {
            TimeZoneData tzData = boundForm.get();
            return redirect(routes.TimeController.index())
                .addingToSession(request, "timezone", tzData.getTimeZone());
        }
    }

    public Result now() {
        String date = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        ObjectNode dateObj = Json.newObject().put("dateString", date);
        return ok(Json.toJson(dateObj));
    }

    // call out to local URL as if it's a remote REST API, since timeapi is down
    public CompletionStage<Result> ws(Http.Request request) {
        String url = "http://localhost:9000/now";
        final Optional<String> timezone = request.session().get("timezone");
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

    private String renderTime(Http.Request request) {
        final Optional<String> timezone = request.session().get("timezone");
        final ZoneId zoneId = zoneId(timezone);
        final Instant instant = clock.instant();
        final ZonedDateTime zdt = instant.atZone(zoneId);
        return formattedDate(zdt);
    }

    private ZoneId zoneId(Optional<String> timezone) {
        return timezone.map(ZoneId::of).orElse(ZoneId.systemDefault());
    }

    private String formattedDate(ZonedDateTime zdt) {
        return zdt.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
