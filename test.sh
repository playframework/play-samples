#!/usr/bin/env bash

# -e          Exit script immediately if any command returns a non-zero exit status.
# -u          Exit script immediately if an undefined variable is used.
# -o pipefail Ensure Bash pipelines return a non-zero status if any of the commands fail,
#             rather than returning the exit status of the last command in the pipeline.
set -euo pipefail

if [ -z "$MATRIX_SCALA" ]; then
    echo "Error: the environment variable MATRIX_SCALA is not set"
    exit 1
fi

# Initialize variables
sample=""
backend_server="pekko"
build_tool="sbt"

# Parse command line arguments
while [[ "$#" -gt 0 ]]; do
  case $1 in
    --build-tool=*) build_tool="${1#*=}" ;;  # Extract the value right of the equal sign
    --backend-server=*) backend_server="${1#*=}" ;;  # Extract the value right of the equal sign
    --sample=*) sample="${1#*=}" ;;  # Extract the value right of the equal sign
    *) echo "Unknown parameter passed: $1"; exit 1 ;;
  esac
  shift  # Move to next argument
done

function buildSample() {
    local dir=$1
    pushd $dir
    if [[ $build_tool == "gradle" ]]; then
      if [ -f scripts/test-gradle ]; then
        scripts/test-gradle
      else
        echo "+-------------------------------+"
        echo "| Executing tests using Gradle  |"
        echo "+-------------------------------+"
        ./gradlew -Dscala.version="$MATRIX_SCALA" check
      fi
    elif [[ $build_tool == "sbt" ]]; then
      if [[ $backend_server == "netty" ]]; then
          find . -name build.sbt | xargs perl -i -pe 's/\/\/\.enablePlugins\(PlayNettyServer\)/.enablePlugins(PlayNettyServer)/g'
      fi
      if [ -f scripts/test-sbt ]; then
        scripts/test-sbt
      else
        echo "+----------------------------+"
        echo "| Executing tests using sbt  |"
        echo "+----------------------------+"
        sbt ++$MATRIX_SCALA test
      fi
    fi
    popd
}

if [ -n "$sample" ]; then
  # Sample is set and not empty
  buildSample $sample
else
  # sample not set, therefore run all samples
  buildSample java/chatroom
  buildSample java/compile-di
  buildSample java/dagger2
  buildSample java/ebean
  buildSample java/fileupload
  buildSample java/forms
  buildSample java/hello-world
  buildSample java/jpa
  buildSample java/rest-api
  buildSample java/starter
  buildSample java/streaming
  buildSample java/websocket
  buildSample java/pekko-cluster
  buildSample play-scala-rest-api-example
  buildSample play-scala-chatroom-example
  buildSample play-scala-compile-di-example
  buildSample play-scala-fileupload-example
  buildSample play-scala-forms-example
  buildSample play-scala-hello-world-tutorial
  buildSample play-scala-log4j2-example
  buildSample play-scala-macwire-di-example
  buildSample play-scala-secure-session-example
  buildSample play-scala-starter-example
  buildSample play-scala-streaming-example
  buildSample play-scala-websocket-example
  if [[ $build_tool == "sbt" ]]; then
#    buildSample play-scala-anorm-example
    buildSample java/grpc
    buildSample play-scala-grpc-example
    buildSample play-scala-isolated-slick-example
    buildSample play-scala-slick-example
    buildSample play-scala-tls-example
  fi
fi
