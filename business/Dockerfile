FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/business-0.0.1-SNAPSHOT.jar /app/business.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/business.jar"]
