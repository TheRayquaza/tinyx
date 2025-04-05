FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
RUN adduser -D repo-user-user
USER repo-user-user
WORKDIR /app
COPY --chown=repo-user-user:repo-user-user . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl repo-user -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D repo-user-user
USER repo-user-user
WORKDIR /app
COPY --from=build app/apps/repo-user/target/repo-user-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
