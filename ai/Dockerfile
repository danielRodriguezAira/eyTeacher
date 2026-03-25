# Usamos una imagen base de Java 21 optimizada (Eclipse Temurin es estándar y ligera)
FROM eclipse-temurin:21-jre-alpine

# Etiqueta opcional para identificar al autor
LABEL maintainer="leinadfonfria@gmail.com"

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR compilado desde nuestro host al contenedor
# (Asegúrate de cambiar 'hintapi-0.0.1-SNAPSHOT.jar' por el nombre real que haya en tu carpeta /target)
COPY target/*.jar ai-0.0.1-SNAPSHOT.jar

# Exponemos el puerto que usa Spring Boot
EXPOSE 8081

# Comando para ejecutar la aplicación
# Usamos variables de entorno para configurar el endpoint de Ollama
ENTRYPOINT ["java", "-jar", "ai-0.0.1-SNAPSHOT.jar"]
