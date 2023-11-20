#!/usr/bin/env bash

set -e

pushd openbox
	mvn clean package
popd

mkdir -p dist

cp openbox/target/*-runner.jar dist/FranklynClient.jar
