/*
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers;

import akka.actor.Cancellable;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public interface JavaTicker {

    default Source<String, ?> getStringSource() {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("HH mm ss");
        final Source<String, Cancellable> tickSource =
            Source.tick(
                Duration.ZERO,
                Duration.of(100L, ChronoUnit.MILLIS),
                "TICK");
        return tickSource.map((tick) -> df.format(ZonedDateTime.now()));
    }

    default Source<JsonNode, ?> getJsonSource() {
        final DateTimeFormatter df = DateTimeFormatter.ISO_INSTANT;
        final Source<String, Cancellable> tickSource = Source.tick(
            Duration.ZERO,
            Duration.of(100L, ChronoUnit.MILLIS),
            "TICK");
        return tickSource.map((tick) -> {
            ObjectNode result = Json.newObject();
            result.put("timestamp", df.format(ZonedDateTime.now()));
            return result;
        });
    }

}
