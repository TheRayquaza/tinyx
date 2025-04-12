#!/bin/bash
set -e

BASE_URL="http://127.0.0.1"

function log() {
  echo -e "\n🔹 $1"
}

function create_user() {
  local username=$1
  local password="Password123!"
  local email="${username}@example.com"
  
  log "Creating user: $username"
  local response=$(curl -s -X POST "$BASE_URL/user" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$username\", \"password\": \"$password\", \"email\": \"$email\"}")
  echo "$response"
  echo "$response" | jq -r .id
}

function get_token() {
  local user_id=$1
  local username=$2
  
  log "Getting token for $username"
  local response=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$username\", \"password\": \"Password123!\"}")
  echo "$response" | jq -r .jwt
}

function create_post() {
  local user_token=$1
  local text=$2
  
  local response=$(curl -s -X POST "$BASE_URL/post" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $user_token" \
    -d "{\"text\": \"$text\"}")
  echo "$response" | jq
}

function test_like_post() {
  log "Test 1: Like a post"
  local like_response=$(curl -s -X POST "$BASE_URL/social/post/$POST2_ID/like" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN")
  echo "$like_response"
}

function test_get_likers() {
  log "Test 2: Get users who liked a post"
  curl -s -X GET "$BASE_URL/social/post/$POST2_ID/like" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_get_liked_posts() {
  log "Test 3: Get posts liked by a user"
  curl -s -X GET "$BASE_URL/social/user/$USER1_ID/like" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_unlike_post() {
  log "Test 4: Unlike a post"
  curl -s -X DELETE "$BASE_URL/social/post/$POST2_ID/like" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n✅ Post unliked\n"
}

function test_verify_unlike() {
  log "Test 5: Verify post is unliked"
  curl -s -X GET "$BASE_URL/social/post/$POST2_ID/like" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_follow_user() {
  log "Test 6: Follow a user"
  local follow_response=$(curl -s -X POST "$BASE_URL/social/user/$USER2_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN")
  echo "$follow_response"
}

function test_get_followers() {
  log "Test 7: Get user followers"
  curl -s -X GET "$BASE_URL/social/user/$USER2_ID/follower" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_get_following() {
  log "Test 8: Get user following"
  curl -s -X GET "$BASE_URL/social/user/$USER1_ID/following" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_unfollow_user() {
  log "Test 9: Unfollow a user"
  curl -s -X DELETE "$BASE_URL/social/user/$USER2_ID/follow" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n✅ User unfollowed\n"
}

function test_verify_unfollow() {
  log "Test 10: Verify user is unfollowed"
  curl -s -X GET "$BASE_URL/social/user/$USER2_ID/follower" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_block_user() {
  log "Test 11: Block a user"
  local block_response=$(curl -s -X POST "$BASE_URL/social/user/$USER2_ID/block" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN")
  echo "$block_response"
}

function test_get_blocked_users() {
  log "Test 12: Get blocked users"
  curl -s -X GET "$BASE_URL/social/user/$USER1_ID/blocked" \
    -H "Authorization: Bearer $USER1_TOKEN" | jq .
}

function test_follow_blocked_user() {
  log "Test 13: Try to follow a blocked user (should fail)"
  curl -s -X POST "$BASE_URL/social/user/$USER2_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" || echo "✅ Failed as expected (403 Forbidden)"
}

function test_like_blocked_user_post() {
  log "Test 14: Try to like a post from a blocked user (should fail)"
  curl -s -X POST "$BASE_URL/social/post/$POST2_ID/like" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" || echo "✅ Failed as expected (403 Forbidden)"
}

function test_get_blocked_user_followers() {
  log "Test 15: Try to get followers of a blocked user (should fail)"
  curl -s -X GET "$BASE_URL/social/user/$USER2_ID/follower" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" || echo "✅ Failed as expected (403 Forbidden)"
}

function test_blocked_user_follow_attempt() {
  log "Test 16: Blocked user tries to follow the blocker (should fail)"
  curl -s -X POST "$BASE_URL/social/user/$USER1_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER2_TOKEN" -w "\n" || echo "✅ Failed as expected (403 Forbidden)"
}

function test_unblock_user() {
  log "Test 17: Unblock a user"
  curl -s -X DELETE "$BASE_URL/social/user/$USER2_ID/block" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n✅ User unblocked\n"
}

function test_self_block() {
  log "Test 18: Try to block yourself (should fail)"
  curl -s -X POST "$BASE_URL/social/user/$USER1_ID/block" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" || echo "✅ Failed as expected (400 Bad Request)"
}

function test_self_follow() {
  log "Test 19: Try to follow yourself (should fail)"
  curl -s -X POST "$BASE_URL/social/user/$USER1_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" || echo "✅ Failed as expected (400 Bad Request)"
}

function test_block_removes_follows() {
  log "Test 20: Test blocking removes follow relationships"

  # First user2 follows user1
  log "User2 follows User1"
  curl -s -X POST "$BASE_URL/social/user/$USER1_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER2_TOKEN"

  # Check user2's following list contains user1
  log "Check user2's following list contains user1"
  curl -s -X GET "$BASE_URL/social/user/$USER2_ID/following" \
    -H "Authorization: Bearer $USER2_TOKEN" | jq .

  # Now user2 blocks user1
  log "User2 blocks User1"
  curl -s -X POST "$BASE_URL/social/user/$USER1_ID/block" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER2_TOKEN"

  # Check user2's following list no longer contains user1
  log "Check user2's following list (should be empty)"
  curl -s -X GET "$BASE_URL/social/user/$USER2_ID/following" \
    -H "Authorization: Bearer $USER2_TOKEN" | jq .
}

function test_relationship_chain() {
  log "Test 21: Testing relationship chain"

  # User3 follows User1
  log "User3 follows User1"
  curl -s -X POST "$BASE_URL/social/user/$USER1_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER3_TOKEN"

  # User3 blocks User4
  log "User3 blocks User4"
  curl -s -X POST "$BASE_URL/social/user/$USER4_ID/block" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER3_TOKEN"

  # Check User3's blocked list
  log "Check User3's blocked list"
  curl -s -X GET "$BASE_URL/social/user/$USER3_ID/blocked" \
    -H "Authorization: Bearer $USER3_TOKEN" | jq .

  # Check User4's blockedBy list
  log "Check User4's blockedBy list"
  curl -s -X GET "$BASE_URL/social/user/$USER4_ID/blockedBy" \
    -H "Authorization: Bearer $USER4_TOKEN" | jq .
}

function test_nonexistent_resources() {
  log "Test 22: Testing nonexistent resources"

  # Try to like nonexistent post
  log "Try to like nonexistent post"
  curl -s -X POST "$BASE_URL/social/post/nonexistent-post/like" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" -o /dev/null -f || echo "✅ Not found (404)"

  # Try to follow nonexistent user
  log "Try to follow nonexistent user"
  curl -s -X POST "$BASE_URL/social/user/nonexistent-user/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n" -o /dev/null -f || echo "✅ Not found (404)"
}

function test_user_deletion() {
  log "Test 23: User deletion behavior"

  # User1 follows User3
  log "User1 follows User3"
  curl -s -X POST "$BASE_URL/social/user/$USER3_ID/follow" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $USER1_TOKEN"

  # Check relationships
  log "Check relationships before deletion"
  curl -s -X GET "$BASE_URL/social/user/$USER3_ID/follower" \
    -H "Authorization: Bearer $USER3_TOKEN" | jq .

  # Delete User1
  log "Delete User1"
  curl -s -X DELETE "$BASE_URL/user" \
    -H "Authorization: Bearer $USER1_TOKEN" -w "\n✅ User deleted\n"

  # Check relationships after deletion
  log "Check relationships after deletion (should be empty)"
  curl -s -X GET "$BASE_URL/social/user/$USER3_ID/follower" \
    -H "Authorization: Bearer $USER3_TOKEN" | jq .
}

# Setup - create test users
log "Creating test users..."
USER1_ID=$(create_user "alice")
USER2_ID=$(create_user "bob")
USER3_ID=$(create_user "carol")
USER4_ID=$(create_user "dave")

# Get tokens for all users
USER1_TOKEN=$(get_token "$USER1_ID" "alice")
USER2_TOKEN=$(get_token "$USER2_ID" "bob")
USER3_TOKEN=$(get_token "$USER3_ID" "carol")
USER4_TOKEN=$(get_token "$USER4_ID" "dave")

# Create test posts
log "Creating test posts..."
POST1_ID=$(create_post "$USER1_TOKEN" "Hello, world!")
POST2_ID=$(create_post "$USER2_TOKEN" "Goodbye, world!")

# Run all tests
test_like_post
test_get_likers
test_get_liked_posts
test_unlike_post
test_verify_unlike
test_follow_user
test_get_followers
test_get_following
test_unfollow_user
test_verify_unfollow
test_block_user
test_get_blocked_users
test_follow_blocked_user
test_like_blocked_user_post
test_get_blocked_user_followers
test_blocked_user_follow_attempt
test_unblock_user
test_self_block
test_self_follow
test_block_removes_follows
test_relationship_chain
test_nonexistent_resources
test_user_deletion

log "All tests completed!"
