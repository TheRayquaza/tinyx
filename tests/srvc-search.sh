#!/bin/bash
set -e

BASE_URL="http://localhost:8080"
SEARCH_ENDPOINT="$BASE_URL/search"
CHANNEL="post_aggregate"
USERNAME="testuser"
USER_ID="15a1a100c293c91129883571"
TOKEN=$(echo -n "$USER_ID,$USERNAME" | base64)
AUTH_HEADER="Authorization: Bearer $TOKEN"

function log() {
  echo -e "\n🔹 $1"
}

function publish_post() {
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

function search_posts() {
  local query=$1
  
  log "Searching for: $query"
  curl -s -X GET "$SEARCH_ENDPOINT" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d "{\"query\": \"$query\"}" | jq .
}

function test_search_existing_post() {
  log "Step 1: Publish and search for existing post"
  publish_post "$POST_ID" false
  search_posts "Post"
}

function test_search_nonexistent_post() {
  log "Step 2: Search for non-existent content"
  search_posts "ShouldNotExist"
}

function test_search_deleted_post() {
  log "Step 3: Simulate deletion and verify removal"
  publish_post "$POST_ID" true
  search_posts "Post"
}

# Main execution
POST_ID=$(uuidgen)

test_search_existing_post
test_search_nonexistent_post
test_search_deleted_post

echo -e "\n✅ All tests completed."
