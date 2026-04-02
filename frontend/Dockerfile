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

# Configuración de Nginx para soportar Angular Routing y proxy de API en HTTP
RUN printf 'server { \n\
    listen 80; \n\
    server_name localhost; \n\
    resolver 127.0.0.11 valid=30s; \n\
    location /api/ { \n\
        set $backend http://eyteacher-backend-app:8080; \n\
        proxy_pass $backend$request_uri; \n\
        proxy_set_header Host $host; \n\
        proxy_set_header X-Real-IP $remote_addr; \n\
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \n\
        proxy_set_header X-Forwarded-Proto $scheme; \n\
        proxy_set_header Authorization $http_authorization; \n\
        proxy_pass_header Authorization; \n\
    } \n\
    location /ai-api/ { \n\
        set $ai http://eyteacher-ai-app:8081; \n\
        rewrite ^/ai-api(/.*) $1 break; \n\
        proxy_pass $ai; \n\
        proxy_set_header Host $host; \n\
        proxy_set_header X-Real-IP $remote_addr; \n\
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \n\
        proxy_set_header X-Forwarded-Proto $scheme; \n\
        proxy_set_header Authorization $http_authorization; \n\
        proxy_pass_header Authorization; \n\
    } \n\
    location / { \n\
        root /usr/share/nginx/html; \n\
        index index.html index.htm; \n\
        try_files $uri $uri/ /index.html; \n\
    } \n\
}' > /etc/nginx/conf.d/default.conf

# Copiar los archivos compilados desde la etapa anterior
# Nota: Ajusta 'frontend' si el nombre del proyecto en angular.json es diferente
COPY --from=build /app/dist/frontend/browser/ /usr/share/nginx/html/

# Exponer el puerto 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
