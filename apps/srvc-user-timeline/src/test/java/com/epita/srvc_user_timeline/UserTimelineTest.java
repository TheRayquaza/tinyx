package com.epita.srvc_user_timeline;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.LikeCommand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTimelineTest {
    private static final String USER_ID_1 = "15a1a100c293c91129883571";
    private static final String USER_ID_2 = "15a1a100c293c91129883572";

    static String TOKEN_USER_1;
    static String TOKEN_USER_2;


    static final String USER_1 = "user-1";
    static final String USER_2 = "user-2";


    static String postId1;
    static String postId2;

    @Inject
    RedisPublisher redisPublisher;

    @Inject
    AuthContext authContext;
    @Inject
    Logger logger;


    void publishAndWait(String channel, Object object) throws InterruptedException {
        redisPublisher.publish(channel, object);
        Thread.sleep(300);
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
        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then()
                .statusCode(200)
                .body("posts", empty());
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

        // Seul son propre post est dans sa timeline
        Response response = given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1);

        System.out.println(response.body().prettyPrint());

        response.then().statusCode(200)
                .body("posts.text", hasItem("Post from user 1"))
                .body("posts.text", not(hasItem("Post from user 2")));
    }

    @Test
    @Order(3)
    void user1LikesPostFromUser2() throws Exception {
        LikeCommand like = new LikeCommand();
        like.setUserId(USER_ID_1);
        like.setPostId(postId2);
        like.setLiked(true);

        publishAndWait("like_command", like);

        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then().statusCode(200)
                .body("posts.text", hasItem("Post from user 2"));
    }

    @Test
    @Order(4)
    void user1UnlikesPostFromUser2() throws Exception {
        LikeCommand unlike = new LikeCommand();
        unlike.setUserId(USER_ID_1);
        unlike.setPostId(postId2);
        unlike.setLiked(false);

        publishAndWait("like_command", unlike);

        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then().statusCode(200)
                .body("posts.text", not(hasItem("Post from user 2")));
    }

    @Test
    @Order(5)
    void user1BlocksUser2() throws Exception {
        BlockCommand block = new BlockCommand();
        block.setUserId(USER_ID_1);
        block.setTargetId(USER_ID_2);
        block.setBlocked(true);

        publishAndWait("block_command", block);

        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then().statusCode(200)
                .body("posts.text", not(hasItem("Post from user 2")));
    }

    @Test
    @Order(6)
    void user1UnblocksUser2AndRelikes() throws Exception {
        BlockCommand unblock = new BlockCommand();
        unblock.setUserId(USER_ID_1);
        unblock.setTargetId(USER_ID_2);
        unblock.setBlocked(false);

        publishAndWait("block_command", unblock);

        LikeCommand relike = new LikeCommand();
        relike.setUserId(USER_ID_1);
        relike.setPostId(postId2);
        relike.setLiked(true);

        publishAndWait("like_command", relike);

        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then().statusCode(200)
                .body("posts.text", hasItem("Post from user 2"));
    }

    @Test
    @Order(7)
    void deleteUser2Account() throws Exception {
        UserAggregate deleteUser = new UserAggregate();
        deleteUser.setId(USER_ID_2);
        deleteUser.setDeleted(true);

        publishAndWait("user_aggregate", deleteUser);

        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then().statusCode(200)
                .body("posts.text", not(hasItem("Post from user 2")));
    }

    @Test
    @Order(8)
    void deleteUser1Account() throws Exception {
        UserAggregate deleteUser = new UserAggregate();
        deleteUser.setId(USER_ID_1);
        deleteUser.setDeleted(true);

        publishAndWait("user_aggregate", deleteUser);

        given()
                .header("Authorization", "Bearer " + TOKEN_USER_1)
                .when().get("/user-timeline/" + USER_ID_1)
                .then().statusCode(200)
                .body("posts", empty());
    }
}
