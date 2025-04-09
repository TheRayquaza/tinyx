package com.epita.srvc_home_timeline;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.exchange.redis.aggregate.PostAggregate;
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

  static String TOKEN_USER_1;
  static String TOKEN_USER_2;

  static final String USER_1 = "user-1";
  static final String USER_2 = "user-2";

  static String postId1;
  static String postId2;

  @Inject RedisPublisher redisPublisher;

  @Inject AuthContext authContext;
  @Inject Logger logger;

  void publishAndWait(String channel, Object object) throws InterruptedException {
    redisPublisher.publish(channel, object);
  }

  @BeforeAll
  public static void setupAuth() {
    TOKEN_USER_1 = AuthService.generateToken(USER_ID_1, USER_1);
    TOKEN_USER_2 = AuthService.generateToken(USER_ID_2, USER_2);
  }

  @BeforeEach
  public void setupAuthContext() {
    AuthEntity authEntity1 = new AuthEntity(USER_ID_1, USER_1);
    authContext.setAuthEntity(authEntity1);
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
  void user1FollowsUser2() throws Exception {
    FollowCommand follow = new FollowCommand();
    follow.setUserId(USER_ID_1);
    follow.setFollowerId(USER_ID_2);
    follow.setFollowing(true);

    System.out.println("user 1 follows user 2");

    publishAndWait("follow_command", follow);
    Thread.sleep(15000);

    System.out.println("user 1 follows user 2 - done");

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200).body("posts.text", hasItem("Post from user 2"));
  }

  @Test
  @Order(4)
  void user1LikesPostFromUser2() throws Exception {
    LikeCommand like = new LikeCommand();
    like.setUserId(USER_ID_1);
    // like.setPostId(postId2);
    like.setLiked(true);

    publishAndWait("like_command", like);

    Response response =
        given().header("Authorization", "Bearer " + TOKEN_USER_1).when().get(USER_ID_1);

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200).body("posts.text", hasItem("Post from user 2"));
  }
}
