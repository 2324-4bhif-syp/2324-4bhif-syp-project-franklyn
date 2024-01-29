#!/usr/bin/env bash

set -e

pushd recorder
  mvn -B clean package
popd

mkdir -p dist/recorder

cp -r recorder/target/*-runner.jar dist/recorder/franklyn-recorder.jar
