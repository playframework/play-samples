#!/usr/bin/env bash

pushd play-java-dagger2-example
zip -r -q ../play-java-dagger2-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-dagger2-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-chatroom-example
zip -r -q ../play-java-chatroom-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-chatroom-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-compile-di-example
zip -r -q ../play-java-compile-di-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-compile-di-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-forms-example
zip -r -q ../play-java-forms-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-forms-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-ebean-example
zip -r -q ../play-java-ebean-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-ebean-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-fileupload-example
zip -r -q ../play-java-fileupload-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-fileupload-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-jpa-example
zip -r -q ../play-java-jpa-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-jpa-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-grpc-example
zip -r -q ../play-java-grpc-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-grpc-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-hello-world-tutorial
zip -r -q ../play-java-hello-world-sbt-tutorial.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-hello-world-gradle-tutorial.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-streaming-example
zip -r -q ../play-java-streaming-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-streaming-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-rest-api-example
zip -r -q ../play-java-rest-api-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-rest-api-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-starter-example
zip -r -q ../play-java-starter-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-starter-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-scala-anorm-example
zip -r -q ../play-scala-anorm-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-scala-anorm-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-websocket-example
zip -r -q ../play-java-websocket-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-websocket-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
popd
pushd play-java-pekko-cluster-example
zip -r -q ../play-java-pekko-cluster-sbt-example.zip . -x "gradle/*" -x "scripts/*" -x "*gradle*"
zip -r -q ../play-java-pekko-cluster-gradle-example.zip . -x "project/*" -x "scripts/*" -x "*.sbt"
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
