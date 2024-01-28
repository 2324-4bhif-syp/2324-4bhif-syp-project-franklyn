#!/usr/bin/env bash

set -e

mkdir -p ~/.ssh
echo "$SSH_PRIVATE_KEY" > $1/private.key
sudo chmod 600 $1/private.key
echo "$SSH_KNOWN_HOSTS" > ~/.ssh/known_hosts
echo "SSH_KEY_PATH=$1/private.key" >> "$GITHUB_ENV"
echo "SSH_USER=$2" >> "$GITHUB_ENV"
echo "SSH_HOST=$3" >> "$GITHUB_ENV"
