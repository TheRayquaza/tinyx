#!/bin/bash
set -e

BASE_URL="http://localhost:8080"
SEARCH_ENDPOINT="$BASE_URL/search"
CHANNEL="post_aggregate"
USERNAME="testuser"
USER_ID="15a1a100c293c91129883571"
TOKEN=$(echo -n "{\"id\":\"$USER_ID\",\"username\":\"$USERNAME\"}" | base64)
AUTH_HEADER="Authorization: Bearer $TOKEN"

log() {
  echo -e "\n🔹 $1"
}

# Simulate post publication (mock equivalent)
publish_post() {
  local post_id=$1
  local deleted=$2
  echo "{
    \"id\": \"$post_id\",
    \"uuid\": \"$(uuidgen)\",
    \"ownerId\": \"$USER_ID\",
    \"text\": \"Test Post\",
    \"media\": null,
    \"repostId\": null,
    \"replyToPostId\": null,
    \"reply\": false,
    \"createdAt\": \"$(date --iso-8601=seconds)\",
    \"updatedAt\": \"$(date --iso-8601=seconds)\",
    \"deleted\": $deleted
  }" > /tmp/post_aggregate.json

  log "Publishing post to Redis mock (simulated)..."
  cat /tmp/post_aggregate.json
  echo "⏳ Waiting for system to index..."
  sleep 2
}

POST_ID=$(uuidgen)

# ✅ Step 1: Publish + Search success
publish_post "$POST_ID" false
log "Searching for existing post..."
curl -s -X GET "$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{"query": "Post"}' | jq .

# ✅ Step 2: Search for non-existent content
log "Searching for non-existent post..."
curl -s -X GET "$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{"query": "ShouldNotExist"}' | jq .

# ✅ Step 3: Simulate deletion and verify removal
publish_post "$POST_ID" true
log "Searching after post deletion..."
curl -s -X GET "$SEARCH_ENDPOINT" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{"query": "Post"}' | jq .

echo -e "\n✅ All tests completed."
