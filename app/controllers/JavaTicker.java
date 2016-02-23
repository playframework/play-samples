package controllers;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public interface JavaTicker {

    default Source<String, NotUsed> getSource() {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("HH mm ss");
        final Source tickSource = Source.tick(Duration.Zero(), Duration.create(100, MILLISECONDS), "TICK");
        final Source<String, NotUsed> eventSource = tickSource.map((tick) -> df.format(ZonedDateTime.now()));
        return eventSource;
    }

}
