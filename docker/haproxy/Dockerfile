FROM haproxy:2.6.6-alpine
USER root
RUN if ! grep haproxy /etc/group; then addgroup -S haproxy; fi;
RUN if ! grep haproxy /etc/passwd; then adduser -SD haproxy -g haproxy; fi;
RUN chown -R haproxy:haproxy /usr/local/etc/haproxy
COPY ./haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg
RUN haproxy -f /usr/local/etc/haproxy/haproxy.cfg -D
