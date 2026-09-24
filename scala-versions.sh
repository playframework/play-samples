#!/usr/bin/env bash

# Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>

readonly scala213Version="2.13.18"
readonly scala3Version="3.3.8"
readonly scala39LTSVersion="3.9.0"
readonly scala3NextVersion="3.10.0-RC2"

readonly -a publishedScalaVersions=("$scala213Version" "$scala3Version")
readonly -a testedScalaVersions=(
  "${publishedScalaVersions[@]}"
  "$scala39LTSVersion"
  "$scala3NextVersion"
)

resolveScalaVersion() {
  case "$1" in
    "2.13.x") printf '%s\n' "$scala213Version" ;;
    "3.3.x")  printf '%s\n' "$scala3Version" ;;
    "3.9.x")  printf '%s\n' "$scala39LTSVersion" ;;
    "3.next") printf '%s\n' "$scala3NextVersion" ;;
    *)         printf '%s\n' "$1" ;;
  esac
}
