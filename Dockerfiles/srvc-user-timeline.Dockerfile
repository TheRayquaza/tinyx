FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
RUN adduser -D srvc-user-timeline-user
USER srvc-user-timeline-user
WORKDIR /app
COPY --chown=srvc-user-timeline-user:srvc-user-timeline-user . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl srvc-user-timeline -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D srvc-user-timeline-user
USER srvc-user-timeline-user
WORKDIR /app
COPY --from=build app/apps/srvc-user-timeline/target/srvc-user-timeline-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
