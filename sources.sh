#!/usr/bin/env bash

# Java Examples
pushd java
pushd chatroom
zip -r -q ../../play-java-chatroom-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-chatroom-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd compile-di
zip -r -q ../../play-java-compile-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-compile-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd dagger2
zip -r -q ../../play-java-dagger2-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-dagger2-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd ebean
zip -r -q ../../play-java-ebean-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-ebean-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd fileupload
zip -r -q ../../play-java-fileupload-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-fileupload-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd forms
zip -r -q ../../play-java-forms-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-forms-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd grpc
zip -r -q ../../play-java-grpc-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-grpc-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd hello-world
zip -r -q ../../play-java-hello-world-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-hello-world-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd jpa
zip -r -q ../../play-java-jpa-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-jpa-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd pekko-cluster
zip -r -q ../../play-java-pekko-cluster-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-pekko-cluster-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd rest-api
zip -r -q ../../play-java-rest-api-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-rest-api-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd starter
zip -r -q ../../play-java-starter-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-starter-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd streaming
zip -r -q ../../play-java-streaming-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-streaming-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd websocket
zip -r -q ../../play-java-websocket-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-java-websocket-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
popd

# Scala Examples
pushd scala
pushd anorm
zip -r -q ../../play-scala-anorm-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-anorm-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd chatroom
zip -r -q ../../play-scala-chatroom-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-chatroom-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd compile-di
zip -r -q ../../play-scala-compile-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-compile-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd fileupload
zip -r -q ../../play-scala-fileupload-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-fileupload-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd forms
zip -r -q ../../play-scala-forms-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-forms-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd grpc
zip -r -q ../../play-scala-grpc-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-grpc-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd hello-world
zip -r -q ../../play-scala-hello-world-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-hello-world-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd isolated-slick
zip -r -q ../../play-scala-isolated-slick-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-isolated-slick-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd log4j2
zip -r -q ../../play-scala-log4j2-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-log4j2-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd macwire-di
zip -r -q ../../play-scala-macwire-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-macwire-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd rest-api
zip -r -q ../../play-scala-rest-api-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-rest-api-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd secure-session
zip -r -q ../../play-scala-secure-session-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-secure-session-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd slick
zip -r -q ../../play-scala-slick-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-slick-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd starter
zip -r -q ../../play-scala-starter-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-starter-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd streaming
zip -r -q ../../play-scala-streaming-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-streaming-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd tls
zip -r -q ../../play-scala-tls-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-tls-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd websocket
zip -r -q ../../play-scala-websocket-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../../play-scala-websocket-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
popd
