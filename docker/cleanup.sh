#!/usr/bin/env zsh

docker ps | grep -v NAMES | awk '{ print $NF }' | xargs docker stop \
&& docker container prune --force \
&& docker images | grep -vE "true|REPO" | awk '{ print $3 }' | xargs docker rmi \
&& echo "Clean complete"
