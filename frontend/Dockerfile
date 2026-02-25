# Etapa 1: Build
FROM node:22-alpine AS build

WORKDIR /app

# Copiar archivos de dependencias primero para aprovechar la caché de capas
COPY package*.json ./
RUN npm install

# Copiar el resto del código y compilar
COPY . .
RUN npm run build

# Etapa 2: Serve con Nginx
FROM nginx:stable-alpine

# Configuración de Nginx para soportar Angular Routing y HTTPS
# Se asume que los certificados se montarán en /etc/nginx/ssl/
RUN echo 'server { \
    listen 80; \
    server_name localhost; \
    return 301 https://$host$request_uri; \
} \
server { \
    listen 443 ssl; \
    server_name localhost; \
    ssl_certificate /etc/nginx/ssl/server.crt; \
    ssl_certificate_key /etc/nginx/ssl/server.key; \
    location /api/ { \
        proxy_pass https://host.docker.internal:8443/api/; \
        proxy_ssl_verify off; \
        proxy_set_header Host $host; \
        proxy_set_header X-Real-IP $remote_addr; \
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \
        proxy_set_header X-Forwarded-Proto $scheme; \
    } \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Copiar los archivos compilados desde la etapa anterior
# Nota: Ajusta 'frontend' si el nombre del proyecto en angular.json es diferente
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Exponer los puertos 80 y 443
EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]
