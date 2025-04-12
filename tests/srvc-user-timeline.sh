#!/bin/bash
set -e

# Configuration
BASE_URL="http://localhost"
TIMELINE_ENDPOINT="$BASE_URL/user-timeline"
REDIS_PUBLISH_ENDPOINT="$BASE_URL/test/publish"

USER_ID_1="15a1a100c293c91129883571"
USER_ID_2="15a1a100c293c91129883572"
USER_1="user-1"
USER_2="user-2"

TOKEN_USER_1=$(echo -n "$USER_ID_1,$USER_1" | base64)
TOKEN_USER_2=$(echo -n "$USER_ID_2,$USER_2" | base64)

AUTH_HEADER_1="Authorization: Bearer $TOKEN_USER_1"
AUTH_HEADER_2="Authorization: Bearer $TOKEN_USER_2"

function log() {
  echo -e "\n🔹 $1"
}

function get_timeline() {
  local user_id=$1
  local auth_header=$2
  
  log "Getting timeline for user: $user_id"
  
  curl -s -X GET "$TIMELINE_ENDPOINT/$user_id" \
    -H "$auth_header" | jq .
}

function assert_timeline_contains() {
  local user_id=$1
  local auth_header=$2
  local text=$3
  local should_contain=$4
  
  log "Asserting timeline for user $user_id $([ "$should_contain" == "true" ] && echo "contains" || echo "does not contain") post: \"$text\""
  
  local response=$(curl -s -X GET "$TIMELINE_ENDPOINT/$user_id" -H "$auth_header")
  echo "$response" | jq .
  
  if [ "$should_contain" == "true" ]; then
    if echo "$response" | jq -e '.posts[].text | contains("'"$text"'")' > /dev/null; then
      echo "✅ Timeline contains post with text: \"$text\""
    else
      echo "❌ Timeline does NOT contain post with text: \"$text\" but should!"
      exit 1
    fi
  else
    if echo "$response" | jq -e '.posts[].text | contains("'"$text"'")' > /dev/null; then
      echo "❌ Timeline contains post with text: \"$text\" but should NOT!"
      exit 1
    else
      echo "✅ Timeline does NOT contain post with text: \"$text\""
    fi
  fi
}

function test_timeline_initially_empty() {
  log "Test 1: Timeline initially empty"
  
  local response=$(curl -s -X GET "$TIMELINE_ENDPOINT/$USER_ID_1" -H "$AUTH_HEADER_1")
  echo "$response" | jq .
  
  if [ "$(echo "$response" | jq '.posts | length')" -eq 0 ]; then
    echo "✅ Timeline is empty"
  else
    echo "❌ Timeline is not empty but should be!"
    exit 1
  fi
}

function test_create_posts() {
  log "Test 2: Create posts"
  
  POST_ID_1=$(uuidgen)
  local post1='{
    "id": "'"$POST_ID_1"'",
    "ownerId": "'"$USER_ID_1"'",
    "text": "Post from user 1",
    "createdAt": "'"$(date --iso-8601=seconds)"'",
    "updatedAt": "'"$(date --iso-8601=seconds)"'"
  }'
  
  POST_ID_2=$(uuidgen)
  local post2='{
    "id": "'"$POST_ID_2"'",
    "ownerId": "'"$USER_ID_2"'",
    "text": "Post from user 2",
    "createdAt": "'"$(date --iso-8601=seconds)"'",
    "updatedAt": "'"$(date --iso-8601=seconds)"'"
  }'
  
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 1" "true"
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 2" "false"
}

function test_user1_likes_post_from_user2() {
  log "Test 3: User 1 likes post from User 2"
  
  local like='{
    "userId": "'"$USER_ID_1"'",
    "postId": "'"$POST_ID_2"'",
    "liked": true
  }'
  
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 2" "true"
}

function test_user1_unlikes_post_from_user2() {
  log "Test 4: User 1 unlikes post from User 2"
  
  local unlike='{
    "userId": "'"$USER_ID_1"'",
    "postId": "'"$POST_ID_2"'",
    "liked": false
  }'
  
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 2" "false"
}

function test_user1_blocks_user2() {
  log "Test 5: User 1 blocks User 2"
  
  local block='{
    "userId": "'"$USER_ID_1"'",
    "targetId": "'"$USER_ID_2"'",
    "blocked": true
  }'
  
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 2" "false"
}

function test_user1_unblocks_user2_and_relikes() {
  log "Test 6: User 1 unblocks User 2 and relikes post"
  
  local unblock='{
    "userId": "'"$USER_ID_1"'",
    "targetId": "'"$USER_ID_2"'",
    "blocked": false
  }'
  
  local relike='{
    "userId": "'"$USER_ID_1"'",
    "postId": "'"$POST_ID_2"'",
    "liked": true
  }'
  
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 2" "true"
}

function test_delete_user2_account() {
  log "Test 7: Delete User 2 account"
  
  local delete_user2='{
    "id": "'"$USER_ID_2"'",
    "deleted": true
  }'
  
  assert_timeline_contains "$USER_ID_1" "$AUTH_HEADER_1" "Post from user 2" "false"
}

function test_delete_user1_account() {
  log "Test 8: Delete User 1 account"
  
  local delete_user1='{
    "id": "'"$USER_ID_1"'",
    "deleted": true
  }'
  
  local response=$(curl -s -X GET "$TIMELINE_ENDPOINT/$USER_ID_1" -H "$AUTH_HEADER_1")
  echo "$response" | jq .
  
  if [ "$(echo "$response" | jq '.posts | length')" -eq 0 ]; then
    echo "✅ Timeline is empty after account deletion"
  else
    echo "❌ Timeline is not empty but should be after account deletion!"
    exit 1
  fi
}

# Main execution
POST_ID_1=""
POST_ID_2=""

test_timeline_initially_empty
test_create_posts
test_user1_likes_post_from_user2
test_user1_unlikes_post_from_user2
test_user1_blocks_user2
test_user1_unblocks_user2_and_relikes
test_delete_user2_account
test_delete_user1_account

log "✅ All user timeline tests completed successfully!"
