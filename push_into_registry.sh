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

check_service_name()
{
    local service_name=$1
    local valid_services=("repo-post" "repo-user" "repo-social" "srvc-search" "srvc-home-timeline" "srvc-user-timeline" "srvc-media")
    for service in "${valid_services[@]}"; do
        if [ "$service_name" == "$service" ]; then
        return 0
        fi
    done
    return 1
}

usage_check "$@"
if ! check_service_name "$2"; then
  echo "Error: Invalid service name '$2'"
  display_guide
  exit 1
fi

DOCKER_TAG=$1
SERVICE=$2
DOCKER_REGISTRY='registry.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-02'
DOCKER_IMAGE="${DOCKER_REGISTRY}/${SERVICE}"

echo "Building image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -f Dockerfiles/${SERVICE}.Dockerfile .
docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
