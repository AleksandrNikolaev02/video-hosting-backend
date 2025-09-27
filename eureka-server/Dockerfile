FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/eureka-server-0.0.1-SNAPSHOT.jar /app/eureka-server.jar

EXPOSE 8889

ENTRYPOINT ["java", "-jar", "/app/eureka-server.jar"]
