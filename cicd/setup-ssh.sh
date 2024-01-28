#!/usr/bin/env bash

set -e

mkdir -p ~/.ssh
echo "$SSH_PRIVATE_KEY" > ${{github.workspace}}/private.key
sudo chmod 600 ${{github.workspace}}/private.key
echo "$SSH_KNOWN_HOSTS" > ~/.ssh/known_hosts
echo "SSH_KEY_PATH=${{github.workspace}}/private.key" >> "$GITHUB_ENV"
echo "SSH_USER=${{secrets.SSH_USERNAME}}" >> "$GITHUB_ENV"
echo "SSH_HOST=${{secrets.SSH_HOST}}" >> "$GITHUB_ENV"
