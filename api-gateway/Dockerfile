FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
