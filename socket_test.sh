#!/usr/bin/env zsh
echo "$@" | socat stdio tcp4-connect:127.0.0.1:9999
