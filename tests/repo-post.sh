#!/bin/bash

set -e

# 🔧 Config
BASE_URL="http://192.168.121.147/post"
TOKEN="Bearer $(echo -n '{"id":"15a1a100c293c91129883571","username":"testuser"}' | base64)" # or hardcode a JWT here

function log() {
  echo -e "\n🔹 $1"
}

# Helper to pretty print responses
function request() {
  METHOD=$1
  ENDPOINT=$2
  shift 2
  echo "➡️ $METHOD $BASE_URL$ENDPOINT"
  resp=$(curl -s -w "\n%{http_code}" -X "$METHOD" "$BASE_URL$ENDPOINT" \
    -H "Authorization: $TOKEN" \
    "$@")

  body=$(echo "$resp" | head -n1)
  code=$(echo "$resp" | tail -n1)

  echo "⬅️ Status: $code"
  echo "$body"
  echo ""
}

# ✅ Test 1: Create post (no media)
log "Creating post (no media)..."
request POST "/" \
  -F 'json=Test Post from curl' \
  -F 'extensions=txt'

# ✅ Test 2: Invalid post (missing fields)
log "Creating invalid post (missing fields)..."
request POST "/"

# ✅ Test 3: Get non-existent post
log "Getting non-existent post..."
request GET "/15a1a660c293c91129883566"

# ✅ Test 4: Upload with file
log "Uploading post with media..."
TMP_FILE=$(mktemp /tmp/testfile.XXXXXX.jpg)
echo "JPEG!" > "$TMP_FILE"
request POST "/" \
  -F "json=Test with media" \
  -F "extensions=jpg" \
  -F "media=@$TMP_FILE;type=image/jpeg"
rm "$TMP_FILE"

log "✅ All tests finished."
