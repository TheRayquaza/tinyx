package com.epita.repo_post;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.repo_post.controller.RepoPostController;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.ReplyPostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestHTTPEndpoint(RepoPostController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestSecurity(authorizationEnabled = false)
class PostControllerTest {
  @Inject AuthContext authContext;

  private static List<String> postIds = new ArrayList<>();
  private static final String TEST_ID = "15a1a100c293c91129883571";
  private static final String TEST_USERNAME = "testuser";
  private static final String TEST_PASSWORD = "Password123!";
  private static final String TEST_EMAIL = "test@example.com";

  @Test
  @Order(1)
  void createPost() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    CreatePostRequest request = new CreatePostRequest();
    request.text = "Test Post";
    request.media = null;

    Response response =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(request)
            .when()
            .post("/create")
            .then()
            .statusCode(200)
            .body("text", is("Test Post"))
            .body("id", is(notNullValue()))
            .extract()
            .response();

    postIds.add(response.jsonPath().getString("id"));
  }

  @Test
  @Order(2)
  void getPostDetails() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/post/" + postIds.get(0))
        .then()
        .statusCode(200)
        .extract()
        .response();
  }

  @Test
  @Order(3)
  void notExistGetPostDetails() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/post/" + postIds.get(0))
        .then()
        .statusCode(404)
        .extract()
        .response();
  }

  @Test
  @Order(4)
  void editPost() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    EditPostRequest request = new EditPostRequest();
    request.text = "Test Post Edited";

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .body(request)
        .when()
        .put("/post/" + postIds.get(0))
        .then()
        .statusCode(200)
        .extract()
        .response();
  }

  @Test
  @Order(5)
  void addReply() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    ReplyPostRequest request = new ReplyPostRequest();
    request.text = "Test Reply";

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .body(request)
        .when()
        .post("/post/" + postIds.get(0) + "/reply")
        .then()
        .statusCode(200)
        .extract()
        .response();
  }

  @Test
  @Order(6)
  void testAllReplies() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/post/" + postIds.get(0) + "/reply")
        .then()
        .statusCode(200)
        .body("size()", is(1))
        .extract()
        .response();
  }

  @Test
  @Order(7)
  void deletePost() {
    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .when()
        .delete("/post/" + postIds.get(0))
        .then()
        .statusCode(204)
        .extract()
        .response();
  }
}
