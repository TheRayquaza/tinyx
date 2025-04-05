#!/bin/bash

display_guide() {
  echo "Usage: $0 <tag> <service_name>"
  echo "Example: $0 v1.0.0 repo-post"
}

if [ "$1" == "--help" ]; then
  display_guide
  exit 0
fi

usage_check() {
  if [ $# -ne 2 ]; then
    display_guide
    exit 1
  fi
}

usage_check "$@"

DOCKER_TAG=$1
SERVICE=$2
DOCKER_REGISTRY='registry.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-02'
DOCKER_IMAGE="${DOCKER_REGISTRY}/${SERVICE}"

echo "Building image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -f Dockerfiles/${SERVICE}.Dockerfile .
docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
