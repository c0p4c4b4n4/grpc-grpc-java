FROM gradle:8-jdk21-alpine AS build

WORKDIR /app

COPY gradlew ./
COPY gradle/ gradle/
COPY settings.gradle ./
COPY eureka-server/build.gradle ./build.gradle

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon
COPY eureka-server/src/ src/
RUN ./gradlew assemble --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
