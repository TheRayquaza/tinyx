# repo-post

## Requirements

- Java 17
- MongoDB
- Minio
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

| Variable                        | Description                                         |
|----------------------------------|-----------------------------------------------------|
| `QUARKUS_LOG_LEVEL`             | Log level                                           |
| `QUARKUS_HTTP_PORT`             | Quarkus HTTP port (e.g., `8080`)                    |
| `QUARKUS_HTTP_HOST`             | Quarkus HTTP host (e.g., `0.0.0.0` or `localhost`)  |
| `QUARKUS_APP_PROFILE`           | Application profile (`dev` or `prod`)               |
| `QUARKUS_REDIS_HOSTS`           | Redis hosts                                         |
| `QUARKUS_REDIS_PASSWORD`        | Redis password                                      |
| `QUARKUS_REDIS_CLIENT_TYPE`     | Redis client type (e.g., `jedis`, `lettuce`)        |
| `QUARKUS_REDIS_RECONNECT_ATTEMPTS` | Number of reconnect attempts for Redis            |
| `QUARKUS_REDIS_AUTO_FAILOVER`   | Enable Redis auto failover (`true` or `false`)      |
| `POST_AGGREGATE_CHANNEL`        | Redis post aggregate channel                        |
| `BLOCK_COMMAND_CHANNEL`         | Redis block command channel                         |
| `QUARKUS_MONGODB_CONNECTION_STRING` | MongoDB connection string                        |
| `S3_ENDPOINT`                   | MinIO endpoint (e.g., `http://localhost:9000`)       |
| `S3_BUCKET`                     | MinIO bucket name (e.g., `default`)                 |
| `S3_ACCESS_KEY`                 | MinIO access key (e.g., `minioadmin`)               |
| `S3_SECRET_KEY`                 | MinIO secret key (e.g., `minioadmin`)               |

