#!/usr/bin/env bash

# Java Examples
pushd java
pushd chatroom
zip -r -q ../chatroom-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../chatroom-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd compile-di
zip -r -q ../compile-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../compile-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd dagger2
zip -r -q ../dagger2-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../dagger2-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd ebean
zip -r -q ../ebean-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../ebean-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd fileupload
zip -r -q ../fileupload-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../fileupload-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd forms
zip -r -q ../forms-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../forms-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd grpc
zip -r -q ../grpc-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../grpc-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd hello-world
zip -r -q ../hello-world-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../hello-world-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd jpa
zip -r -q ../jpa-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../jpa-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd pekko-cluster
zip -r -q ../pekko-cluster-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../pekko-cluster-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd rest-api
zip -r -q ../rest-api-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../rest-api-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd starter
zip -r -q ../starter-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../starter-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd streaming
zip -r -q ../streaming-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../streaming-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd websocket
zip -r -q ../websocket-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../websocket-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
popd

# Scala Examples
pushd play-scala-anorm-example
zip -r -q ../play-scala-anorm-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-anorm-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-fileupload-example
zip -r -q ../play-scala-fileupload-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-fileupload-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-chatroom-example
zip -r -q ../play-scala-chatroom-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-chatroom-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-compile-di-example
zip -r -q ../play-scala-compile-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-compile-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-hello-world-tutorial
zip -r -q ../play-scala-hello-world-sbt-tutorial.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-hello-world-gradle-tutorial.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-forms-example
zip -r -q ../play-scala-forms-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-forms-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-grpc-example
zip -r -q ../play-scala-grpc-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-grpc-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-isolated-slick-example
zip -r -q ../play-scala-isolated-slick-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-isolated-slick-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-log4j2-example
zip -r -q ../play-scala-log4j2-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-log4j2-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-macwire-di-example
zip -r -q ../play-scala-macwire-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-macwire-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-rest-api-example
zip -r -q ../play-scala-rest-api-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-rest-api-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-secure-session-example
zip -r -q ../play-scala-secure-session-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-secure-session-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-slick-example
zip -r -q ../play-scala-slick-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-slick-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-starter-example
zip -r -q ../play-scala-starter-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-starter-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-streaming-example
zip -r -q ../play-scala-streaming-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-streaming-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-tls-example
zip -r -q ../play-scala-tls-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-tls-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-websocket-example
zip -r -q ../play-scala-websocket-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-websocket-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
