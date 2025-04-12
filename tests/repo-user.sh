#!/bin/bash
set -e

BASE_URL="http://127.0.0.1"
USERNAME="testuser"
PASSWORD="Password123!"
EMAIL="test@example.com"
BIO="This is my updated bio"

function log() {
  echo -e "\n🔹 $1"
}

function create_user() {
  log "Creating user..."
  local CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/user" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\", \"email\": \"$EMAIL\"}")
  
  echo "$CREATE_RESPONSE"
  local USER_ID=$(echo "$CREATE_RESPONSE" | jq -r .id)
  echo "User ID: $USER_ID"
  
  return "$USER_ID"
}

function login() {
  log "Logging in..."
  local LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}")
  
  echo "$LOGIN_RESPONSE"
  local TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r .jwt)
  echo "Token received"
  
  AUTH_HEADER="Authorization: Bearer $TOKEN"
}

function get_current_user() {
  log "Getting current user..."
  curl -s -X GET "$BASE_URL/user" -H "$AUTH_HEADER" | jq .
}

function get_user_by_id() {
  local USER_ID=$1
  log "Getting user by ID..."
  curl -s -X GET "$BASE_URL/user/$USER_ID" -H "$AUTH_HEADER" | jq .
}

function update_user() {
  log "Updating user..."
  curl -s -X PUT "$BASE_URL/user" -H "$AUTH_HEADER" -H "Content-Type: application/json" \
    -d "{\"username\": \"${USERNAME}updated\", \"bio\": \"$BIO\"}" | jq .
}

function upload_profile_image() {
  log "Uploading profile image..."
  local IMAGE_FILE=$(mktemp /tmp/profile.XXXXXX.jpg)
  echo "JPEG DATA" > "$IMAGE_FILE"
  
  local UPLOAD_RESPONSE=$(curl -s -X PUT "$BASE_URL/user/image" -H "$AUTH_HEADER" \
    -F "file=@$IMAGE_FILE;type=image/jpeg")
  
  echo "$UPLOAD_RESPONSE" | jq .
  rm "$IMAGE_FILE"
}

function delete_user() {
  log "Deleting user..."
  curl -s -X DELETE "$BASE_URL/user" -H "$AUTH_HEADER" -w "\n✅ Deleted\n"
}

function verify_user_deletion() {
  local USER_ID=$1
  log "Verifying user deletion..."
  curl -s -X GET "$BASE_URL/user/$USER_ID" -H "$AUTH_HEADER" -w "\n" -o /dev/null -f || echo "✅ User not found (expected 404)"
}

# Main execution
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/user" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\", \"email\": \"$EMAIL\"}")
echo "$CREATE_RESPONSE"
USER_ID=$(echo "$CREATE_RESPONSE" | jq -r .id)

login
get_current_user
get_user_by_id "$USER_ID"
update_user
upload_profile_image
delete_user
verify_user_deletion "$USER_ID"
