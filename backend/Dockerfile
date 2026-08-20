FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} api.jar
COPY wait-for-it.sh .

RUN apk add --no-cache bash && chmod +x wait-for-it.sh

EXPOSE 8080
CMD ["java", "-jar", "api.jar"]
