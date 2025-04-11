# INFO8

## Test

```
cd apps
mvn test
```

## Deploy

## Structure

```
.
├── apps
│   ├── exchange                      # exchange library
│   │   └── target/reports/apidocs/   # Javadoc documentation
│   ├── pom.xml
│   ├── repo-post
│   │   └── target/reports/apidocs/   # Javadoc documentation
│   ├── repo-social
│   │   └── target/reports/apidocs/   # Javadoc documentation
│   ├── repo-user
│   │   └── target/reports/apidocs/   # Javadoc documentation
│   ├── srvc-home-timeline
│   │   └── target/reports/apidocs/   # Javadoc documentation
│   ├── srvc-search
│   │   └── target/reports/apidocs/   # Javadoc documentation
│   ├── srvc-user-timeline
│   │   └── target/reports/apidocs/   # Javadoc documentation
├── ci                                # all cis for each service / k8s resources
├── doc                               # misc documentation (swagger, datamodel)
├── k8s
│   ├── apps                          # deployment of each apps
│   ├── elastic                       # deployment of elastic
│   ├── kustomize.yml
│   ├── mongodb                       # deployment of sharded mongo
│   ├── neo4j                         # deployment of neo4j
│   ├── minio-cluster                 # deployment of minio
│   └── redis                         # deployment of redis
├── mvnw
├── README.md
```

##

| Project                            | Members                                   |
| ---------------------------------- | ----------------------------------------- |
| **Repo-User / Exchange**           | Aurélien, Matéo, Khaled, Samy, Maxim      |
| **Infra Mongo**                    | Matéo, Samy, Khaled                       |
| **CI (lint, test, build, deploy)** | Samy, (Armand)                            |
| **Repo-Post**                      | Khaled, Maxim, Armand, Baptiste, Aurélien |
| **Repo-Social**                    | Achille, Gab M, Matéo                     |
| **Infra S3**                       | Équipe SRS (Alexis, Gab T, Tim, Noé)      |
| **Infra Neo4j**                    | Achille, Matéo, Samy / Baptiste           |
| **Infra Redis**                    | Matéo, Samy / Baptiste                    |
| **Srvc-Search**                    | Armand, Matéo, Gab M                      |
| **Srvc-home-timeline**             | Anton, Baptiste                           |
| **Srvc-user-timeline**             | Équipe SRS (Alexis, Gab T, Tim, Noé)      |
| **Authentification**               | Équipe SRS (Alexis, Gab T, Tim, Noé)      |
