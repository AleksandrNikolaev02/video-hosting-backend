FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/camunda-service-0.0.1-SNAPSHOT.jar /app/camunda-service.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "/app/camunda-service.jar"]
