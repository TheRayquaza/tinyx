# srvc-search

## Requirements

- Java 17
- MongoDB
- Redis
- Elastic

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

| Variable                                  | Description                          |
|-------------------------------------------|--------------------------------------|
| `SRVC_SEARCH_LOG_LEVEL`                   | Log level (e.g., `INFO`)             |
| `SRVC_SEARCH_QUARKUS_HTTP_PORT`           | Quarkus HTTP port (e.g., `8080`)     |
| `SRVC_SEARCH_APP_PROFILE`                 | Application profile (`dev` / `prod`) |
| `SRVC_SEARCH_REPO_POST_AGGREGATE_CHANNEL` | Post aggregate channel               |
| `SRVC_SEARCH_BLOCK_COMMAND_CHANNEL`       | Block command channel                |
| `SRVC_SEARCH_REDIS_HOSTS`                 | Redis hosts                          |
| `SRVC_SEARCH_REDIS_PASSWORD`              | Redis Password                       |
| `SRVC_SEARCH_MONGODB_ENDPOINTS`           | MongoDB endpoint host(s)             |
| `SRVC_SEARCH_MONGODB_DATABASE`            | MongoDB database name (`SrvcSearch`) |
| `SRVC_SEARCH_MONGODB_COLLECTION`          | MongoDB collection (`SearchModel`)   |
| `SRVC_SEARCH_ELASTIC_INDEX`               | Elastic index                        |
| `SRVC_SEARCH_ELASTIC_HOST`                | Elastic host                         |
| `SRVC_SEARCH_ELASTIC_USERNAME`            | Elastic username                     |
| `SRVC_SEARCH_ELASTIC_PASSWORD`            | Elastic password                     |
