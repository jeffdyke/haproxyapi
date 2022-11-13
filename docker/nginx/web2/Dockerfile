FROM nginx:1.23.2-alpine
RUN if ! grep www-data /etc/group; then addgroup -S www-data; fi;
RUN if ! grep www-data /etc/passwd; then adduser -SD www-data -g www-data -h /var/www/html; fi
RUN nginx
COPY ./web2/nginx/nginx.conf /etc/nginx/nginx.conf
COPY ./web2/nginx/sites.conf /etc/nginx/conf.d/sites.conf
COPY ./web2/nginx/index.html /var/www/html/index.html
RUN nginx