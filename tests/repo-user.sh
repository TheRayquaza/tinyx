#!/bin/bash
set -e

BASE_URL="http://192.168.121.147"
USERNAME="tesuser"
PASSWORD="Password123!"
EMAIL="test@example.com"
BIO="This is my updated bio"
IMAGE_FILE=$(mktemp /tmp/profile.XXXXXX.jpg)
echo "JPEG DATA" > "$IMAGE_FILE"

function log() {
  echo -e "\n🔹 $1"
}

log "Creating user..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/user" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\", \"email\": \"$EMAIL\"}")

echo "$CREATE_RESPONSE"
USER_ID=$(echo "$CREATE_RESPONSE" | jq -r .id)

log "Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}")
echo "$LOGIN_RESPONSE"
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r .jwt)
AUTH_HEADER="Authorization: Bearer $TOKEN"

log "Getting current user..."
curl -s -X GET "$BASE_URL/user" -H "$AUTH_HEADER" | jq .

log "Getting user by ID..."
curl -s -X GET "$BASE_URL/user/$USER_ID" -H "$AUTH_HEADER" | jq .

log "Updating user..."
curl -s -X PUT "$BASE_URL/user" -H "$AUTH_HEADER" -H "Content-Type: application/json" \
  -d "{\"username\": \"${USERNAME}updated\", \"bio\": \"$BIO\"}" | jq .

log "Uploading profile image..."
UPLOAD_RESPONSE=$(curl -s -X PUT "$BASE_URL/user/image" -H "$AUTH_HEADER" \
  -F "file=@$IMAGE_FILE;type=image/jpeg")
echo "$UPLOAD_RESPONSE" | jq .

log "Deleting user..."
curl -s -X DELETE "$BASE_URL/user" -H "$AUTH_HEADER" -w "\n✅ Deleted\n"

log "Verifying user deletion..."
curl -s -X GET "$BASE_URL/user/$USER_ID" -H "$AUTH_HEADER" -w "\n" -o /dev/null -f || echo "✅ User not found (expected 404)"

rm "$IMAGE_FILE"
