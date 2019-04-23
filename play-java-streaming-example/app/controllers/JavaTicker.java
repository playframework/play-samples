/*
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers;

import akka.NotUsed;
import akka.actor.Cancellable;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import scala.concurrent.duration.Duration;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public interface JavaTicker {

    default Source<String, ?> getStringSource() {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("HH mm ss");
        final Source<String, Cancellable> tickSource = Source.tick(Duration.Zero(), Duration.create(100, MILLISECONDS), "TICK");
        return tickSource.map((tick) -> df.format(ZonedDateTime.now()));
    }

    default Source<JsonNode, ?> getJsonSource() {
        final DateTimeFormatter df = DateTimeFormatter.ISO_INSTANT;
        final Source<String, Cancellable> tickSource = Source.tick(Duration.Zero(), Duration.create(100, MILLISECONDS), "TICK");
        return tickSource.map((tick) -> {
            ObjectNode result = Json.newObject();
            result.put("timestamp", df.format(ZonedDateTime.now()));
            return result;
        });
    }

}
