FROM openjdk:17-jdk-slim
WORKDIR /usr/src/app
COPY .. .
RUN ./gradlew build
CMD ["java", "-jar", "build/libs/app.jar"]