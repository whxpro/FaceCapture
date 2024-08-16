#!/bin/bash
# 用法： ./build.sh [debug|release|google]
cleanup() {
  echo "run git clean"
  git reset --hard HEAD && \
  git clean -fd
}
trap cleanup EXIT

buildType=$1

if [ "$buildType" == "google" ]; then
  echo "build google"
  ./gradlew clean && \
  ./gradlew xmlClassGuardRelease bundleRelease
elif [ "$buildType" == "release" ]; then
  echo "build release"
  ./gradlew clean && \
  ./gradlew xmlClassGuardRelease assembleRelease
else
  echo "build debug"
  ./gradlew clean && \
  ./gradlew xmlClassGuardRelease assembleDebug
fi