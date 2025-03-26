# Creating my app

## MVC pattern

If ensure how to create a service, you can follow the MVC (Model-View-Controller) pattern
* **Model** layer is responsible for retrieving business data (accessing models within Mongo, Neo4j ...)
* **Controller** layer is responsible for manipulating the Model layer to handle business logic
* **View** layer is responsible for handling any input/output of the service (structuring output for REST endpoint, Catching Redis Aggregates to update projection database for instance)

## Code Structure

Here is a sample of code structure (according to scale's tds) to implement a robust MVC:

* `controller` refers to the **View** layer. It contains
  * **`XXXControllerApi`**: the interface storing all APIs endpoints, including API spec
  * **`XXXController`**: the implementation of the API
  * `request/response`: the actual input/output of the api
* `repository` refers to the **Model** layer. It contains:
  * `models`: actual models stored within db (can be any of course)
  * **`XXXrepository`**: should manage creation of optimized / complex queries
* `service` refers to the **Controller** layer: It contains:
  * `entity`: can be any kind of data that service is manipulating.
  * **`XXXService`**: business logic core of the service
* **`XXXErrorCode`**: custom error codes of your service implementing ErrorCode of exchange
* `converter`: converters to transform entity to response, model to entity ...
```
.
└── xxx
    ├── controller
    │   ├── XXXControllerApi.java
    │   ├── XXXController.java
    │   ├── request/
    │   └── response/
    ├── converter
    ├── repository
    │   ├── model/
    │   └── XXXRepository.java
    ├── XXXErrorCode.java
    └── service
        ├── entity
        └── XXXService.java
```

Entity seems redundant when we could just use request/response or model, but by design they are different:

* **Models**: Represents how data is stored in the database.
  * Should not be directly exposed to external consumers.
  * Contains database-related annotations and relationships.
* **Request/Response**: Defines the input/output of the API endpoints.
  * Can have transformations such as: renaming fields, removing sensitive data
  * Ensures compatibility and flexibility.
* **Entity**: Represents business logic without linked to database or API.
  * Used within the service layer for processing.
  * Prevents views from modifying entities directly.

We may of course modify that structure if you disagree, I just copied it from the tds anyway

## Lint

```bash
python3 -m venv .venv
source ./.venv/bin/activate
pip install -r requirements.txt

# Will lint your code and install a pre-commit hook to lint it automatically
pre-commit run --all-file
pre-commit install
```

## POM

POM is just your java package manager (just like `npm`, `poetry`, `cargo`). 

There is a root pom that includes the basics: 
* Web framework: `quarkus`, `jackson`, `jakarta`, ...
* Common frameworks: `redis`, `minio`, `mongo`
* Utils: open api spec builder, jacoco for report, junit for testing
* Shared Library: `exchange`  

you can add your own dependencies in your `pom.xml` in  (I am thinking of `neo4j` for repo-social for instance)

## Application Properties

You can "inject" env variable into application properties. Example I can inject `REPO_USER_HOST_REDIS` with this application.properties:

```
# Quarkus Logs
quarkus.analytics.disabled=true
quarkus.log.level=INFO

# Quarkus Swagger & Others
quarkus.swagger-ui.always-include=true
quarkus.resteasy-reactive.path=/api

# Quarkus Redis Configuration
quarkus.redis.devservices.enabled=false
quarkus.redis.hosts=${repo.user.redis.host:localhost}:6379 # just here
...
```

## Dockerfile

* Use explicit tag (not **latest**)
* Use light image (**alpine** is nice)
* Optimize your layers as much as possible
* Use uber jar (mvn )

## Kubernetes

Refers to your td of kubernetes or the `repo-user` deployment

```
xxx
├── deployment.yml
├── ingress.yml
├── kustomize.yml
├── service.yml
└── ....yml
```
