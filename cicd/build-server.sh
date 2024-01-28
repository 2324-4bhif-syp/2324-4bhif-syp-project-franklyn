#!/usr/bin/env bash

set -e

pushd server
  mvn -B clean package
popd

mkdir -p dist/server

cp -r server/target/*-runner.jar dist/server/franklyn-server.jar
