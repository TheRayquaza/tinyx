# TinyX Platform

Welcome to the TinyX Platform, a distributed social media application built with microservices architecture.

## Table of Contents
- [Project Structure](#project-structure)
- [Team Structure](#team-structure)
- [Documentation](#documentation)
- [Architecture](#architecture)
- [Data Model](#data-model)
- [Testing](#testing)
- [Deployment](#deployment)
- [Deployment Specifications](#deployment-specifications)

## Project Structure

```
.
├── apps
│   ├── exchange                 # exchange library
│   ├── pom.xml
│   ├── repo-post
│   ├── repo-social
│   ├── repo-user
│   ├── srvc-home-timeline
│   ├── srvc-search
│   ├── srvc-user-timeline
├── tests                        # tests in production
├── ci                           # ci for each app
├── Dockerfiles                  # dockerfiles for each app
├── doc
├── k8s
│   ├── apps                     # deployment of each apps
│   ├── elastic                  # deployment of elastic
│   ├── kustomize.yml
│   ├── mongodb                  # deployment of sharded mongo
│   ├── neo4j                    # deployment of neo4j
│   ├── minio-cluster            # deployment of minio cluster
│   └── redis                    # deployment of redis cluster
├── mvnw
├── README.md
```

## Team Structure

| Component | Team Members |
|-----------|--------------|
| **Repo-User / Exchange** | Aurélien, Matéo, Khaled, Samy, Maxim |
| **Infra Mongo** | Matéo, Samy, Khaled |
| **CI (lint, test, build, deploy)** | Samy, (Armand) |
| **Repo-Post** | Khaled, Maxim, Armand, Baptiste, Aurélien |
| **Repo-Social** | Achille, Gab M, Matéo |
| **Infra S3** | Équipe SRS (Alexis, Gab T, Tim, Noé) |
| **Infra Neo4j** | Achille, Matéo, Samy / Baptiste |
| **Infra Redis** | Matéo, Samy / Baptiste |
| **Srvc-Search** | Armand, Gab M |
| **Srvc-home-timeline** | Anton, Baptiste |
| **Srvc-user-timeline** | Équipe SRS (Alexis, Gab T, Tim, Noé) |
| **Authentication** | Équipe SRS (Alexis, Gab T, Tim, Noé) |

## Documentation

All project documentation is stored in the `doc` directory, including:
- `swaggers`: API specifications for each service and the aggreations of all
- `arch`: System architecture diagrams
- `datamodel`: Data model schemas
- `misc`: internal misc doc for the whole team

Other documentation can be found in:
- `apps/<svc>/target/reports/apidocs`: Javadoc report
- `apps/<svc>/target/jacoco-report`: Jacoco report for coverage
- `apps/<svc>/README.md`: deployment spec, misc doc about the service

## Architecture

The project is built with a microservices architecture including the following components :

- `repo-user`: User account and login management
- `repo-post`: Post creation and storage
- `repo-social`: Social graph and relationships
- `srvc-home-timeline`: Aggregated timeline of followed users
- `srvc-user-timeline`: User-specific timeline
- `srvc-search`: Search functionality across posts
- `port-frontend`: Small React front app (distributed by nginx)
- `exchange`: Common library for inter-service communication

![Architecture Diagram](doc/ARCH_v1.2.pdf)

## Data Model

We use different databases optimized for our specific needs:

- MongoDB (sharded): Post and user data storage
- Neo4j: Social graph and relationships
- Redis: Pub Sub
- Minio: Media storage for repo-user and repo-post, downloading via srvc-media
- Elasticsearch: Search of post using a query

![Data Model](doc/DATAMODEL_v1.1.png)

## Testing

### Testing all apps

To run tests for all services:

```bash
cd apps
docker compose up -d
mvn test
docker compose down
```

### Testing individual app

Individual service tests can be run by navigating to the specific service directory and executing:

```bash
cd apps/<service-name>
docker compose up -d
mvn test
docker compose down
```

### Testing in production

After k8s cluster deployed (`kubectl apply -k k8s/`), you can start the production tests using:

```bash
bash tests/repo-user.sh
bash tests/repo-post.sh
bash tests/repo-social.sh
bash tests/srvc-search.sh
bash tests/srvc-user-timeline.sh
bash tests/srvc-home-timeline.sh
```

## Deployment

TinyX is deployed on Kubernetes. The deployment process is handled through our CI/CD pipeline.

To deploy the whole stack, just use:

```bash
kubectl apply -k k8s/
```

In case something is wrong with redis / mongo / minio:

```bash
kubectl delete all -all -n <redis/mongo/minio>
kubectl apply -k k8s/
```

In case crash loop back off occurs (especially when redis / mongo are down before starting pod), you can just restart all pods with:

```bash
kubectl delete pods -all -n apps
```

## Deployment Specifications

We provide a sample of good preset for our architecture.

Note that Elastic Search and Neo4j should also have cluster configuration to handle more than 1M users' demand

### Development Environment
This is the default configuration currently setup:

**1 K8S node**

| Service | Replicas | CPU (cores) | Memory | Storage | Database Configuration |
|---------|----------|-------------|--------|---------|------------------------|
| **Apps** |
| repo-user | 1 | 0.5 | 512MB | - | MongoDB (single) |
| repo-post | 1 | 0.5 | 512MB | - | MongoDB (single) |
| repo-social | 1 | 0.5 | 512MB | - | Neo4j (single) |
| srvc-home-timeline | 1 | 0.5 | 512MB | - | Redis (cluster) |
| srvc-user-timeline | 1 | 0.5 | 512MB | - | Redis (cluster) |
| srvc-search | 1 | 0.5 | 768MB | - | Elasticsearch (single) |
| port-frontend | 1 | 0.5 | 256MB | - | - |
| exchange | library | - | - | - | - |
| **Databases** |
| MongoDB | 1 | 1 | 1GB | 10GB | 3-node replica set |
| Neo4j | 1 | 1 | 1GB | 5GB | Single instance |
| Redis | 3 | 0.5 | 512MB | 1GB | 3 node Cluster |
| Elasticsearch | 1 | 1 | 2GB | 5GB | Single instance |
| MinIO | 3 | 0.5 | 512MB | 20GB | Cluster |

**Total Resource Requirements:** 11 cores, 10GB memory, 41GB storage

### Staging Environment
**At least 3 K8S nodes**

| Service | Replicas | CPU (cores) | Memory | Storage | Database Configuration |
|---------|----------|-------------|--------|---------|------------------------|
| **Apps** |
| repo-user | 2 | 1 | 1GB | - | MongoDB (replica set) |
| repo-post | 2 | 1 | 1GB | - | MongoDB (replica set) |
| repo-social | 2 | 1 | 1GB | - | Neo4j (cluster) |
| srvc-home-timeline | 2 | 1 | 768MB | - | Redis (cluster) |
| srvc-user-timeline | 2 | 1 | 768MB | - | Redis (cluster) |
| srvc-search | 2 | 1 | 1GB | - | Elasticsearch (cluster) |
| port-frontend | 3 | 0.5 | 125MB | - | - |
| exchange | library | - | - | - | - |
| **Databases** |
| MongoDB | 3 | 2 | 2GB | 20GB | 3-node replica set |
| Neo4j | 3 | 2 | 2GB | 10GB | Single node (should be clustered) |
| Redis | 3 | 1 | 1GB | 2GB | 3-node cluster |
| Elasticsearch | 3 | 2 | 3GB | 15GB | 3-node cluster |
| MinIO | 4 | 1 | 1GB | 50GB | 4-node distributed setup |

**Total Resource Requirements:** 30 cores, 22GB memory, 97GB storage

### Production Environment
**3-5 K8S nodes**

| Service | Replicas | CPU (cores/min-max) | Memory (min-max) | Storage | Database Configuration |
|---------|----------|---------------------|------------------|---------|------------------------|
| **Apps** |
| repo-user | 3-6 | 2-4 | 2-4GB | - | MongoDB (sharded) |
| repo-post | 3-8 | 2-4 | 2-4GB | - | MongoDB (sharded) |
| repo-social | 3-5 | 2-4 | 2-4GB | - | Neo4j (cluster) |
| srvc-home-timeline | 3-10 | 2-4 | 2-4GB | - | Redis (cluster), MongoDB (sharded) |
| srvc-user-timeline | 3-8 | 2-4 | 2-4GB | - | Redis (cluster), MongoDB (sharded) |
| srvc-search | 3-6 | 2-4 | 3-6GB | - | Elasticsearch, MongoDB (sharded) |
| port-frontend | 6 | 0.5 | 250MB | - | - |
| exchange | library | - | - | - | - |
| **Databases** |
| MongoDB | 9 | 4 | 8GB | 100GB | Sharded (9 shards, 3 replicas per shard) |
| Neo4j | 5 | 4 | 8GB | 50GB | Single node (should be clustered) |
| Redis | 7 | 2 | 4GB | 10GB | 5-node cluster with 2 sentinels |
| Elasticsearch | 6 | 4 | 8GB | 100GB | Single node (should be clustered) |
| MinIO | 8 | 2 | 4GB | 500GB | 8-node clustered |

**Total Resource Requirements (Minimum):**
- **Apps**: 40 cores, 40GB memory
- **Databases**: 110 cores, 220GB memory, 760GB storage

**Total: 150 cores, 260GB memory, 760GB storage**
