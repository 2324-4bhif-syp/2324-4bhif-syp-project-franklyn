#!/usr/bin/env bash

set -e

sed -i "s/websocket.url=.*/websocket.url=wss:\/\/franklyn.ddns.net\/api/g" openbox/src/main/resources/application.properties
sed -i "s/http.url=.*/http.url=https:\/\/franklyn.ddns.net\/api/g" openbox/src/main/resources/application.properties

