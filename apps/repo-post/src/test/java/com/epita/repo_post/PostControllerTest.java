package com.epita.repo_post;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.repo_post.controller.RepoPostController;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
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

  @Test
  @Order(1)
  void createPost() throws IOException {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();

    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .multiPart("media", testFile, "application/octet-stream")
            .multiPart("json", "Test Post", "application/json")
            .multiPart("extensions", "jpeg", "application/json")
            .when()
            .post("/")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response
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
  void createPost_no_media() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .multiPart("json", "Test Post 2", "application/json")
            .when()
            .post("/")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response
        .then()
        .statusCode(200)
        .body("text", is("Test Post 2"))
        .body("id", is(notNullValue()))
        .extract()
        .response();

    postIds.add(response.jsonPath().getString("id"));
  }

  @Test
  @Order(3)
  void createPost_no_details() {
    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .when()
            .post("/")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response
        .then()
        .statusCode(RepoPostErrorCode.INVALID_POST_DATA.getHttpCode()) // 400
        .extract()
        .response();
  }

  @Test
  @Order(4)
  void getPostDetails() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    Response response =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .when()
            .get(postIds.get(0))
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200).extract().response();
  }

  @Test
  @Order(5)
  void getPostDetails_not_found() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .when()
            .get("15a1a660c293c91129883566")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(RepoPostErrorCode.POST_NOT_FOUND.getHttpCode()).extract().response();
  }

  @Test
  @Order(6)
  void editPost() throws IOException {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();

    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .multiPart("media", testFile, "application/octet-stream")
            .multiPart("json", "Test Post Edited", "application/json")
            .multiPart("extension", "jpg", "application/json")
            .when()
            .put(postIds.get(0))
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200).extract().response();
  }

  @Test
  @Order(7)
  void editPost_not_found() throws IOException {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();
    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .multiPart("json", "Test Post Edited", "application/json")
            .multiPart("extension", "png", "application/json")
            .multiPart("media", testFile, "application/octet-stream")
            .when()
            .put("post_id_non_existent")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(RepoPostErrorCode.POST_NOT_FOUND.getHttpCode()).extract().response();
  }

  @Test
  @Order(8)
  void addReply() throws IOException {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();

    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .multiPart("media", testFile, "application/octet-stream")
            .multiPart("json", "Test Reply", "application/json")
            .multiPart("extension", "jpg", "application/json")
            .when()
            .post(postIds.get(0) + "/reply")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200).extract().response();
  }

  @Test
  @Order(9)
  void addReply_no_media_text() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);

    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.MULTIPART)
            .header("Authorization", "Bearer " + token)
            .when()
            .post(postIds.get(0) + "/reply")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response
        .then()
        .statusCode(RepoPostErrorCode.INVALID_POST_DATA.getHttpCode())
        .extract()
        .response();
  }

  @Test
  @Order(10)
  void testAllReplies() {

    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .when()
            .get(postIds.get(0) + "/reply")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(200).body("size()", is(1));
  }

  @Test
  @Order(11)
  void deletePost() {
    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .when()
            .delete(postIds.get(0))
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(204).extract().response();
  }

  @Test
  @Order(12)
  void deletePost_notFound() {
    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);

    Response response =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .when()
            .delete(postIds.get(0))
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(RepoPostErrorCode.POST_NOT_FOUND.getHttpCode()).extract().response();
  }
}
