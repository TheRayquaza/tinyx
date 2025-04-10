package com.epita.srvc_search;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.srvc_search.controller.SrvcSearchController;
import com.epita.srvc_search.controller.request.SearchRequest;
import com.epita.srvc_search.service.entity.SearchEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestHTTPEndpoint(SrvcSearchController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestSecurity(authorizationEnabled = false)
public class SrvcSearchTest {

  @Inject AuthContext authContext;

  @Inject RedisPublisher redisPublisher;

  @ConfigProperty(name = "repo.post.aggregate.channel")
  @Inject
  String postAggregateChannel =
      Optional.ofNullable(System.getenv("SRVC_SEARCH_REPO_POST_AGGREGATE_CHANNEL"))
          .orElse("post_aggregate");

  private String token = "";
  private static final String TEST_ID = "15a1a100c293c91129883571";
  private static final String TEST_USERNAME = "testuser";

  private static final List<String> postIds = new ArrayList<>();

  private void publishPostAggregate(String postId, boolean deleted) {
    PostAggregate postAggregate = new PostAggregate();
    postAggregate.setId(postId);
    postAggregate.setUuid(UUID.randomUUID());
    postAggregate.setOwnerId(authContext.getAuthEntity().getUserId());
    postAggregate.setText("Test Post");
    postAggregate.setMedia(null);
    postAggregate.setRepostId(null);
    postAggregate.setReplyToPostId(null);
    postAggregate.setReply(false);
    postAggregate.setUpdatedAt(LocalDateTime.now());
    postAggregate.setCreatedAt(LocalDateTime.now());
    postAggregate.setDeleted(deleted);

    redisPublisher.publish(postAggregateChannel, postAggregate);
    try {
      // Wait for the message to be processed
      Thread.sleep(2000); // Adjust the sleep time as needed
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (deleted) {
      postIds.remove(postId);
      return;
    }
    postIds.add(postId);
  }

  @BeforeEach
  public void setUp() {
    token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
  }

  @Test
  @Order(1)
  void testGetPost() {
    String postId = new ObjectId().toString();
    publishPostAggregate(postId, false);
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery("Post");

    // Extract the response as an array of SearchEntity
    SearchEntity[] searchResults =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(searchRequest)
            .when()
            .post("/search")
            .then()
            .statusCode(200)
            .extract()
            .as(SearchEntity[].class);

    assertEquals(1, searchResults.length);
    assertEquals(postId, searchResults[0].getId());
  }

  @Test
  @Order(2)
  void testGetUnkownPost() {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery("ShouldNotExist");

    // Extract the response as an array of SearchEntity
    SearchEntity[] searchResults =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(searchRequest)
            .when()
            .post("/search")
            .then()
            .statusCode(200)
            .extract()
            .as(SearchEntity[].class);

    assertEquals(0, searchResults.length);
  }

  @Test
  @Order(3)
  void testGetUnknownPostAfterDeletion() {
    publishPostAggregate(postIds.get(0), true);
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setQuery("Post");

    // Extract the response as an array of SearchEntity
    SearchEntity[] searchResults =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(searchRequest)
            .when()
            .post("/search")
            .then()
            .statusCode(200)
            .extract()
            .as(SearchEntity[].class);

    assertEquals(0, searchResults.length);
  }
}
