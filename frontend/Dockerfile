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
    location /api/ { \n\
        proxy_pass http://eyteacher-backend-app:8081/api/; \n\
        proxy_set_header Host $host; \n\
        proxy_set_header X-Real-IP $remote_addr; \n\
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \n\
        proxy_set_header X-Forwarded-Proto $scheme; \n\
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
