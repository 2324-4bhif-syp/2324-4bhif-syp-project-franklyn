#!/usr/bin/env bash

set -e

pushd instructor-client
  npm install
  npx ng build --configuration production --base-href /instructor/ instructor-client
popd

mkdir -p dist/instructor-client

cp -r instructor-client/dist/instructor-client/browser/* dist/instructor-client/
