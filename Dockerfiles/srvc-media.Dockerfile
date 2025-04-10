FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
RUN adduser -D srvc-media-user
USER srvc-media-user
WORKDIR /app
COPY --chown=srvc-media-user:srvc-media-user . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl srvc-media -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D srvc-media-user
USER srvc-media-user
WORKDIR /app
COPY --from=build app/apps/srvc-media/target/srvc-media-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
