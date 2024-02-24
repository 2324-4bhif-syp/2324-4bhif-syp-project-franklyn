#!/usr/bin/env bash

set -e

sed -i "s/serverBaseUrl.*/serverBaseUrl: '\/api',/g" instructor-client/env/environment.ts

