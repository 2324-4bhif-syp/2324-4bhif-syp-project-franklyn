#!/usr/bin/env bash

set -e

sed -i "s/serverBaseUrl.*/serverBaseUrl: '\/api\/examinees',/g" instructor-client/env/environment.ts

