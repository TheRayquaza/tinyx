FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
RUN adduser -D repo-post-user
USER repo-post-user
WORKDIR /app
COPY --chown=repo-post-user:repo-post-user . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl repo-post -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D repo-post-user
USER repo-post-user
WORKDIR /app
COPY --from=build app/apps/repo-post/target/repo-post-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
