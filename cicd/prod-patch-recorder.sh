#!/usr/bin/env bash

set -e

sed -i "s/quarkus.rest-client.server-address.url=.*/quarkus.rest-client.server-address.url=https:\/\/franklyn.ddns.net\/api/g" recorder/src/main/resources/application.properties

