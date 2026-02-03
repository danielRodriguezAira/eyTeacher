# Etapa de compilación
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiamos los archivos necesarios para el wrapper primero (para aprovechar el caché de Docker)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Damos permisos de ejecución al script del wrapper
RUN chmod +x mvnw

# Descargamos las dependencias (esto fallará si no hay src, así que mejor copiar lo necesario)
COPY src ./src

# Usamos el wrapper para compilar
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]