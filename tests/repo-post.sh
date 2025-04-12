#!/bin/bash
set -e

# Configuration
BASE_URL="http://127.0.0.1/post"
TOKEN="Bearer $(echo -n '15a1a100c293c91129883571,testuser' | base64)"

# Helper functions
function log() {
  echo -e "\n🔹 $1"
}

function request() {
  local METHOD=$1
  local ENDPOINT=$2
  shift 2
  
  echo "➡️ $METHOD $BASE_URL$ENDPOINT"
  
  local resp=$(curl -s -w "\n%{http_code}" -X "$METHOD" "$BASE_URL$ENDPOINT" \
    -H "Authorization: $TOKEN" \
    "$@")
  
  local body=$(echo "$resp" | head -n1)
  local code=$(echo "$resp" | tail -n1)
  
  echo "⬅️ Status: $code"
  echo "$body"
  echo ""
}

# Test functions
function test_create_post_no_media() {
  log "Creating post (no media)..."
  request POST "/" \
    -F 'json=Test Post from curl' \
    -F 'extensions=txt'
}

function test_create_invalid_post() {
  log "Creating invalid post (missing fields)..."
  request POST "/"
}

function test_get_nonexistent_post() {
  log "Getting non-existent post..."
  request GET "/15a1a660c293c91129883566"
}

function test_create_post_with_media() {
  log "Uploading post with media..."
  local TMP_FILE=$(mktemp /tmp/testfile.XXXXXX.jpg)
  echo "JPEG!" > "$TMP_FILE"
  
  request POST "/" \
    -F "json=Test with media" \
    -F "extensions=jpg" \
    -F "media=@$TMP_FILE;type=image/jpeg"
  
  rm "$TMP_FILE"
}

# Main execution
test_create_post_no_media
test_create_invalid_post
test_get_nonexistent_post
test_create_post_with_media

log "✅ All tests finished."
