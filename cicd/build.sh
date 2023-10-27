#!/usr/bin/env bash

set -e

pushd client
	mvn clean package
popd

mkdir -p dist

cp client/target/*-runner.jar dist/FranklynClient.jar
