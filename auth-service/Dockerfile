FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/auth-service.jar"]