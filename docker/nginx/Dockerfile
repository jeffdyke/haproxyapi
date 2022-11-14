FROM nginx:1.23.2-alpine
ARG SERVER_NAME
RUN if ! grep www-data /etc/group; then addgroup -S www-data; fi \
  && if ! grep www-data /etc/passwd; then adduser -SD www-data -g www-data -h /var/www/html; fi \
  && nginx -t

COPY ./servers/nginx/nginx.conf /etc/nginx/nginx.conf
COPY ./servers/nginx/sites.conf /etc/nginx/conf.d/sites.conf
COPY ./servers/nginx/index.html /var/www/html/index.html
RUN for f in /var/www/html/index.html /var/www/html/index.html /var/www/html/index.html; do \
  echo $f \
  sed -i"" 's:SERVER_NAME_REPLACE:${SERVER_NAME}:g' $f; \
done && rm -f /etc/ngxix/conf.d/default.conf
RUN nginx