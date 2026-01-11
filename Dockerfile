FROM node:20-bookworm AS assets
WORKDIR /app

COPY package.json package-lock.json postcss.config.js ./
COPY src/main/resources/static/css ./src/main/resources/static/css
COPY src/main/resources/templates ./src/main/resources/templates

RUN npm ci --ignore-scripts
RUN npm run build:css

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
COPY --from=assets /app/src/main/resources/static/css/_build.css ./src/main/resources/static/css/_build.css

RUN ./gradlew bootJar -x buildTailwind -x npmInstall

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar
RUN mkdir -p /app/uploads

VOLUME ["/app/uploads"]
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
