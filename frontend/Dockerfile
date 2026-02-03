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

# Configuración de Nginx para soportar Angular Routing (evita 404 al recargar)
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Copiar los archivos compilados desde la etapa anterior
# Nota: Ajusta 'frontend' si el nombre del proyecto en angular.json es diferente
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Exponer el puerto 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
