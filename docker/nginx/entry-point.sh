#!/bin/sh

apk add bash
/default.conf"
bash -c "envsubst < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf"