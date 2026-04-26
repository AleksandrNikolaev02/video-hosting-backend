FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/email-service-0.0.1-SNAPSHOT.jar /app/email-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/email-service.jar"]
