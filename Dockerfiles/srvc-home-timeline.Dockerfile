FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
RUN adduser -D srvc-home-timeline-user
USER srvc-home-timeline-user
WORKDIR /app
COPY --chown=srvc-home-timeline-user:srvc-home-timeline-user . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl srvc-home-timeline -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D srvc-home-timeline-user
USER srvc-home-timeline-user
WORKDIR /app
COPY --from=build app/apps/srvc-home-timeline/target/srvc-home-timeline-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
