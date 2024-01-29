#!/usr/bin/env bash

set -e

pushd openbox
  mvn -B clean package
popd

mkdir -p dist/openbox

cp -r openbox/target/*-runner.jar dist/openbox/franklyn-student-client.jar
