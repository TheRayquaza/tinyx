package com.epita.srvc_home_timeline;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.FollowCommand;
import com.epita.exchange.redis.command.LikeCommand;
import com.epita.srvc_home_timeline.controller.HomeTimelineController;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestHTTPEndpoint(HomeTimelineController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestSecurity(authorizationEnabled = false)
public class HomeTimelineTest {
  private static final String USER_ID_1 = "15a1a100c293c91129883571";
  private static final String USER_ID_2 = "15a1a100c293c91129883572";
  private static final String USER_ID_3 = "15a1a100c293c91129883573";
  private static final String USER_ID_4 = "15a1a100c293c91129883573";

  static String TOKEN_USER_1;
  static String TOKEN_USER_2;
  static String TOKEN_USER_3;
  static String TOKEN_USER_4;

  static final String USER_1 = "user-1";
  static final String USER_2 = "user-2";
  static final String USER_3 = "user-3";
  static final String USER_4 = "user-4";

  static String postId1;
  static String postId2;

  @Inject RedisPublisher redisPublisher;

  @Inject AuthContext authContext;
  @Inject Logger logger;

  void likePost(String user, String postId, boolean liked) throws InterruptedException {
    LikeCommand like = new LikeCommand();
    like.setUserId(USER_ID_1);
    like.setPostId(postId);
    like.setLiked(liked);

    publishAndWait("like_command", like);
  }

  void followUser(String userId, String follower, boolean following) throws InterruptedException {
    FollowCommand follow = new FollowCommand();
    follow.setUserId(userId);
    follow.setFollowerId(follower);
    follow.setFollowing(true);

    publishAndWait("follow_command", follow);
  }

  void publishAndWait(String channel, Object object) throws InterruptedException {
    redisPublisher.publish(channel, object);
    Thread.sleep(300);
  }

  @BeforeAll
  public static void setupAuth() {
    TOKEN_USER_1 = AuthService.generateToken(USER_ID_1, USER_1);
    TOKEN_USER_2 = AuthService.generateToken(USER_ID_2, USER_2);
    TOKEN_USER_3 = AuthService.generateToken(USER_ID_3, USER_3);
    TOKEN_USER_4 = AuthService.generateToken(USER_ID_4, USER_4);
  }

  @BeforeEach
  public void setupAuthContext() {
    AuthEntity authEntity1 = new AuthEntity(USER_ID_1, USER_1);
    authContext.setAuthEntity(authEntity1);
  }

  @BeforeEach
  void setupUsers() throws InterruptedException {
    UserAggregate user1 = new UserAggregate();
    user1.setId(USER_ID_1);
    user1.setUsername(USER_1);
    user1.setCreatedAt(LocalDateTime.now());
    user1.setUpdatedAt(LocalDateTime.now());
    user1.setDeleted(false);
    UserAggregate user2 = new UserAggregate();
    user2.setId(USER_ID_2);
    user2.setUsername(USER_2);
    user2.setCreatedAt(LocalDateTime.now());
    user2.setUpdatedAt(LocalDateTime.now());
    user2.setDeleted(false);
    UserAggregate user3 = new UserAggregate();
    user3.setId(USER_ID_3);
    user3.setUsername(USER_3);
    user3.setCreatedAt(LocalDateTime.now());
    user3.setUpdatedAt(LocalDateTime.now());
    user3.setDeleted(false);
    UserAggregate user4 = new UserAggregate();
    user4.setId(USER_ID_4);
    user4.setUsername(USER_4);
    user4.setCreatedAt(LocalDateTime.now());
    user4.setUpdatedAt(LocalDateTime.now());
    user4.setDeleted(false);
    System.out.println("user 1 : " + user1);
    publishAndWait("user_aggregate", user1);
    System.out.println("user 2 : " + user2);
    publishAndWait("user_aggregate", user2);
    System.out.println("user 3 : " + user3);
    publishAndWait("user_aggregate", user3);
    System.out.println("user 4 : " + user4);
    publishAndWait("user_aggregate", user4);
  }

  @Test
  @Order(1)
  void timelineInitiallyEmpty() {
    logger.error("token user 1" + TOKEN_USER_1);
    AuthEntity authEntity1 = new AuthEntity(USER_ID_1, USER_1);
    authContext.setAuthEntity(authEntity1);
    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);

    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200).body("hometimeline.entries", empty());
  }

  @Test
  @Order(2)
  void createPosts() throws Exception {
    postId1 = new ObjectId().toString();
    PostAggregate post1 = new PostAggregate();
    post1.setId(postId1);
    post1.setOwnerId(USER_ID_1);
    post1.setText("Post from user 1");
    post1.setCreatedAt(LocalDateTime.now());
    post1.setUpdatedAt(LocalDateTime.now());

    postId2 = new ObjectId().toString();
    PostAggregate post2 = new PostAggregate();
    post2.setId(postId2);
    post2.setOwnerId(USER_ID_2);
    post2.setText("Post from user 2");
    post2.setCreatedAt(LocalDateTime.now());
    post2.setUpdatedAt(LocalDateTime.now());

    publishAndWait("post_aggregate", post1);
    publishAndWait("post_aggregate", post2);

    // Les hometimelines devrait toujours etre vide
    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);

    System.out.println(response.body().prettyPrint());
  }

  @Test
  @Order(3)
  void user3FollowsUser1() throws Exception {
    followUser(USER_ID_1, USER_ID_3, true);

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200);
    // .body("posts.text", hasItem("Post from user 2"));
  }

  @Test
  @Order(4)
  void user1LikesPostFromUser2() throws Exception {
    likePost(USER_ID_1, postId2, true);

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200);
    // .body("posts.text", hasItem("Post from user 2"));
  }

  @Test
  @Order(5)
  void user1UnlikesPostFromUser2() throws Exception {
    likePost(USER_ID_1, postId2, false);

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200);
    // .body("posts.text", not(hasItem("Post from user 2")));
  }

  @Test
  @Order(6)
  void user1ReLikesPostFromUser2() throws Exception {
    likePost(USER_ID_1, postId2, true);

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200);
    // .body("posts.text", hasItem("Post from user 2"));
  }

  @Test
  @Order(7)
  void user3UnfollowsUser1() throws Exception {
    followUser(USER_ID_1, USER_ID_3, true);

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200);
    // .body("posts.text", not(hasItem("Post from user 2")));
  }

  @Test
  @Order(8)
  void complexTest() throws Exception {

    // User 3 follows user 2
    followUser(USER_ID_2, USER_ID_3, true);

    // User 3 follows user 1
    followUser(USER_ID_1, USER_ID_3, true);

    // Checking user 2 home timelines
    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_2).when().get(USER_ID_2);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.followers.text", hasItem("15a1a100c293c91129883573"));

    // User 2 posts
    String newpostId2 = new ObjectId().toString();
    PostAggregate newpost2 = new PostAggregate();
    newpost2.setId(newpostId2);
    newpost2.setOwnerId(USER_ID_2);
    newpost2.setText("new Post from user 2");
    newpost2.setCreatedAt(LocalDateTime.now());
    newpost2.setUpdatedAt(LocalDateTime.now());
    publishAndWait("post_aggregate", newpost2);
    System.out.println("user 2 posts - done");

    // Checking user 3 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200);
    // .body("hometimeline.entries.text", hasItem("new Post from user 2"));

    // User 1 likes post from user 2
    likePost(USER_ID_1, newpostId2, true);

    // Checking user 3 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", hasItem("new Post from user 2"));

    // User 3 unfollows user 1
    followUser(USER_ID_1, USER_ID_3, false);

    // Checking user 3 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", hasItem("new Post from user 2"));

    // User 3 unfollows user 2
    followUser(USER_ID_2, USER_ID_3, false);

    // Checking user 3 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_3).when().get(USER_ID_3);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", not(hasItem("new Post from user 2")));
  }

  @Test
  @Order(9)
  void complexTest2() throws Exception {
    // User 1 follows user 2 and 3
    followUser(USER_ID_2, USER_ID_1, true);
    followUser(USER_ID_3, USER_ID_1, true);

    // User 4 posts
    String newpostId4 = new ObjectId().toString();
    PostAggregate newpost4 = new PostAggregate();
    newpost4.setId(newpostId4);
    newpost4.setOwnerId(USER_ID_4);
    newpost4.setText("new Post from user 4");
    newpost4.setCreatedAt(LocalDateTime.now());
    newpost4.setUpdatedAt(LocalDateTime.now());
    publishAndWait("post_aggregate", newpost4);
    System.out.println("user 4 posts - done");

    // Checking user 1 home timelines
    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", not(hasItem("new Post from user 4")));

    // User 2 and 3 likes post from user 4
    likePost(USER_ID_2, newpostId4, true);
    likePost(USER_ID_3, newpostId4, true);

    // Checking user 1 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", hasItem("new Post from user 4"));

    // User 2 unlikes post from user 4
    likePost(USER_ID_2, newpostId4, false);
    // Checking user 1 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", hasItem("new Post from user 4"));
    // User 3 unlikes post from user 4
    likePost(USER_ID_3, newpostId4, false);
    // Checking user 1 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", not(hasItem("new Post from user 4")));
  }

  @Test
  @Order(10)
  void blockTest() throws Exception {
    // User 1 follows user 2
    followUser(USER_ID_2, USER_ID_1, true);

    // User 2 posts
    String newpostId2 = new ObjectId().toString();
    PostAggregate newpost2 = new PostAggregate();
    newpost2.setId(newpostId2);
    newpost2.setOwnerId(USER_ID_2);
    newpost2.setText("new Post from user 2");
    newpost2.setCreatedAt(LocalDateTime.now());
    newpost2.setUpdatedAt(LocalDateTime.now());
    publishAndWait("post_aggregate", newpost2);
    System.out.println("user 2 posts - done");

    // User 1 blocks user 2
    BlockCommand block = new BlockCommand();
    block.setUserId(USER_ID_1);
    block.setTargetId(USER_ID_2);
    block.setBlocked(true);
    publishAndWait("block_command", block);

    // Checking user 1 home timelines
    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", not(hasItem("new Post from user 2")));

    // User 1 unblocks user 2
    block.setBlocked(false);
    publishAndWait("block_command", block);

    // User 2 posts
    String newpostId2_2 = new ObjectId().toString();
    PostAggregate newpost2_2 = new PostAggregate();
    newpost2_2.setId(newpostId2_2);
    newpost2_2.setOwnerId(USER_ID_2);
    newpost2_2.setText("new Post from user 2");
    newpost2_2.setCreatedAt(LocalDateTime.now());
    newpost2_2.setUpdatedAt(LocalDateTime.now());
    publishAndWait("post_aggregate", newpost2_2);
    System.out.println("user 2 posts - done");

    // Checking user 1 home timelines
    response = given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);
    System.out.println(response.body().prettyPrint());
    response.then().statusCode(200);
    // .body("hometimeline.entries.text", hasItem("new Post from user 2"));
  }
}
