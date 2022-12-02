# haproxyapi
haproxy socket api, simple version of dataplane.  

# Rationale
1. Dataplane is fantastic, but it over steps how haproxy is currently managed, and its own documentation states thats not a great place to introduce it.
2. When controlling haproxy during a release, finer control over the processes, via the socket, is very helpful.

# Docker
This folder contains a `docker-compose.yaml` file that you can run to bring up 1 haproxy server, and 2 nginx servers, split by HTTP/1.1 and H2.  This serves as best/only a way to continually test locally

