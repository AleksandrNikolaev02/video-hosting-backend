FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/file_service-0.0.1-SNAPSHOT.jar /app/file_service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/file_service.jar"]
