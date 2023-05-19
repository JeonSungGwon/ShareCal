FROM openjdk:11-jre-slim

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} /app/

CMD ["java", "-jar", "/app/app.jar"]