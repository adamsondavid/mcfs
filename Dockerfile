FROM gradle:8.14-jdk21 AS build
WORKDIR /build
ENV GRADLE_USER_HOME=/home/gradle/.gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN gradle --no-daemon dependencies
COPY . .
RUN gradle --no-daemon buildServer

FROM azul/zulu-openjdk:21-jre
WORKDIR /app
COPY --from=build /build/build/dist/ ./
EXPOSE 25565
ENTRYPOINT ["java", "-jar", "spigot.jar", "nogui"]
