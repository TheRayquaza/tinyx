FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache maven
RUN adduser -D repo-social-user
USER repo-social-user
WORKDIR /app
COPY --chown=repo-social-user:repo-social-user . .
RUN cd apps && mvn install -N && mvn install -pl exchange && mvn package -pl repo-social -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D repo-social-user
USER repo-social-user
WORKDIR /app
COPY --from=build app/apps/repo-social/target/repo-social-1.0.0-SNAPSHOT-runner.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
