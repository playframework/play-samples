#!/usr/bin/env bash
if [ -z "$MATRIX_SCALA" ]; then
    echo "Error: the environment variable MATRIX_SCALA is not set"
    exit 1
fi
pushd play-java-chatroom-example        && scripts/test-gradle && popd
pushd play-java-compile-di-example      && scripts/test-gradle && popd
pushd play-java-dagger2-example         && scripts/test-gradle && popd
pushd play-java-ebean-example           && scripts/test-gradle && popd
pushd play-java-fileupload-example      && scripts/test-gradle && popd
pushd play-java-forms-example           && scripts/test-gradle && popd
# pushd play-java-grpc-example            && scripts/test-gradle && popd
# pushd play-java-hello-world-tutorial    && scripts/test-gradle && popd
# pushd play-java-jpa-example             && scripts/test-gradle && popd
pushd play-java-rest-api-example        && scripts/test-gradle && popd
# pushd play-java-starter-example         && scripts/test-gradle && popd
pushd play-java-streaming-example       && scripts/test-gradle && popd
pushd play-java-websocket-example       && scripts/test-gradle && popd
# pushd play-java-pekko-cluster-example    && scripts/test-gradle && popd
#pushd play-scala-anorm-example          && scripts/test-gradle && popd
# pushd play-scala-chatroom-example       && scripts/test-gradle && popd
# pushd play-scala-compile-di-example     && scripts/test-gradle && popd
# pushd play-scala-fileupload-example     && scripts/test-gradle && popd
# pushd play-scala-forms-example          && scripts/test-gradle && popd
# pushd play-scala-grpc-example           && scripts/test-gradle && popd
# pushd play-scala-hello-world-tutorial   && scripts/test-gradle && popd
# pushd play-scala-isolated-slick-example && scripts/test-gradle && popd
# pushd play-scala-log4j2-example         && scripts/test-gradle && popd
# pushd play-scala-macwire-di-example     && scripts/test-gradle && popd
pushd play-scala-rest-api-example       && scripts/test-gradle && popd
# Uses libsodium
# pushd play-scala-secure-session-example && scripts/test-gradle && popd
# pushd play-scala-slick-example          && scripts/test-gradle && popd
# pushd play-scala-starter-example        && scripts/test-gradle && popd
# pushd play-scala-streaming-example      && scripts/test-gradle && popd
# uses vanilla sbt
# pushd play-scala-tls-example            && scripts/test-gradle && popd
# pushd play-scala-websocket-example      && scripts/test-gradle && popd
