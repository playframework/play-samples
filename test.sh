#!/usr/bin/env bash
if [ -z "$MATRIX_SCALA" ]; then
    echo "Error: the environment variable MATRIX_SCALA is not set"
    exit 1
fi
pushd play-java-chatroom-example        && scripts/test-sbt && popd
pushd play-java-compile-di-example      && scripts/test-sbt && popd
pushd play-java-dagger2-example         && scripts/test-sbt && popd
pushd play-java-ebean-example           && scripts/test-sbt && popd
pushd play-java-fileupload-example      && scripts/test-sbt && popd
pushd play-java-forms-example           && scripts/test-sbt && popd
if [ "$MATRIX_SCALA" != "3.x" ]; then
  pushd play-java-grpc-example            && scripts/test-sbt && popd
fi
pushd play-java-hello-world-tutorial    && scripts/test-sbt && popd
pushd play-java-jpa-example             && scripts/test-sbt && popd
pushd play-java-rest-api-example        && scripts/test-sbt && popd
pushd play-java-starter-example         && scripts/test-sbt && popd
pushd play-java-streaming-example       && scripts/test-sbt && popd
pushd play-java-websocket-example       && scripts/test-sbt && popd
pushd play-java-akka-cluster-example    && scripts/test-sbt && popd
pushd play-scala-anorm-example          && scripts/test-sbt && popd
pushd play-scala-chatroom-example       && scripts/test-sbt && popd
pushd play-scala-compile-di-example     && scripts/test-sbt && popd
pushd play-scala-fileupload-example     && scripts/test-sbt && popd
pushd play-scala-forms-example          && scripts/test-sbt && popd
if [ "$MATRIX_SCALA" != "3.x" ]; then
  pushd play-scala-grpc-example           && scripts/test-sbt && popd
fi
pushd play-scala-hello-world-tutorial   && scripts/test-sbt && popd
if [ "$MATRIX_SCALA" != "3.x" ]; then
  pushd play-scala-isolated-slick-example && scripts/test-sbt && popd
fi
pushd play-scala-log4j2-example         && scripts/test-sbt && popd
pushd play-scala-macwire-di-example     && scripts/test-sbt && popd
pushd play-scala-rest-api-example       && scripts/test-sbt && popd
# Uses libsodium
pushd play-scala-secure-session-example && scripts/test-sbt && popd
if [ "$MATRIX_SCALA" != "3.x" ]; then
  pushd play-scala-slick-example          && scripts/test-sbt && popd
fi
pushd play-scala-starter-example        && scripts/test-sbt && popd
pushd play-scala-streaming-example      && scripts/test-sbt && popd
# uses vanilla sbt
pushd play-scala-tls-example            && scripts/test-sbt && popd
pushd play-scala-websocket-example      && scripts/test-sbt && popd
