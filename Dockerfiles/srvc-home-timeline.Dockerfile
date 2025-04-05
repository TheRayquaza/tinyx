FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app
COPY . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl srvc-home-timeline -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build app/apps/srvc-home-timeline/target/srvc-home-timeline-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
