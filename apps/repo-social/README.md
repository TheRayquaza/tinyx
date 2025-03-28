# repo-social

## Requirements

- Java 17
- MongoDB
- Neo4j
- Redis

## Development

You should use the given Docker Compose to run the dependencies of the app.

```shell script
docker compose up --build
```

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
mvn package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Configuration

For a deployment in production, you must configure the following environment
variables.

| Variable                      | Description                                        |
|-------------------------------|----------------------------------------------------|
| `REPO_SOCIAL_REDIS_HOST`        | Redis host                                         |
| `REPO_SOCIAL_REDIS_PASSWORD`    | Redis Password                                     |
| `REPO_SOCIAL_MONGODB_HOST`      | MongoDB server host                                |
| `REPO_SOCIAL_MONGODB_DATABASE`  | MongoDB database name                              |
| `REPO_SOCIAL_QUARKUS_HTTP_PORT` | Quarkus HTTP port (e.g., `8080`)                   |
| `REPO_SOCIAL_APP_PROFILE`       | Application profile (`dev` / `prod`)               |
