# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="leinadfonfria@gmail.com"
WORKDIR /app
COPY --from=build /app/target/*.jar ai-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "ai-0.0.1-SNAPSHOT.jar"]
