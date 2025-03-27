package com.epita.repo_user.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import com.epita.repo_user.service.UserService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import java.io.File;
import java.io.File;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestHTTPEndpoint(RepoUserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

  @Inject UserService userService;
  @Inject UserService userService;

  // @Inject
  // S3Service s3Service;

  // @Inject
  // RedisPublisher redisPublisher;

  @Inject AuthContext authContext;

  private static String userId;
  private static final String TEST_USERNAME = "testuser";
  private static final String TEST_PASSWORD = "Password123!";
  private static final String TEST_EMAIL = "test@example.com";

  @Test
  @Order(1)
  @DisplayName("Should create a new user successfully")
  public void testCreateUser() {
    CreateUserRequest request = new CreateUserRequest();
    request.username = TEST_USERNAME;
    request.password = TEST_PASSWORD;
    request.email = TEST_EMAIL;

    Response response =
        given()
  // @Inject
  // S3Service s3Service;

  // @Inject
  // RedisPublisher redisPublisher;

  @Inject AuthContext authContext;

  private static String userId;
  private static final String TEST_USERNAME = "testuser";
  private static final String TEST_PASSWORD = "Password123!";
  private static final String TEST_EMAIL = "test@example.com";

  @Test
  @Order(1)
  @DisplayName("Should create a new user successfully")
  public void testCreateUser() {
    CreateUserRequest request = new CreateUserRequest();
    request.username = TEST_USERNAME;
    request.password = TEST_PASSWORD;
    request.email = TEST_EMAIL;

    Response response =
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/user")
            .then()
            .statusCode(201)
            .body("username", equalTo(TEST_USERNAME))
            .body("email", equalTo(TEST_EMAIL))
            .body("id", notNullValue())
            .extract()
            .response();

    userId = response.jsonPath().getString("id");
  }

  @Test
  @Order(2)
  @DisplayName("Should login successfully")
  public void testLogin() {
    LoginRequest request = new LoginRequest();
    request.username = TEST_USERNAME;
    request.password = TEST_PASSWORD;

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/login")
        .then()
        .statusCode(200)
        .body("username", equalTo(TEST_USERNAME))
        .body("id", notNullValue());
  }

  @Test
  @Order(3)
  @DisplayName("Should get current user successfully")
  public void testGetCurrentUser() {
    given()
        .when()
        .get("/user")
        .then()
        .statusCode(200)
        .body("username", equalTo(TEST_USERNAME))
        .body("email", equalTo(TEST_EMAIL));
  }

  @Test
  @Order(4)
  @DisplayName("Should get user by ID successfully")
  public void testGetUserById() {
    given()
        .pathParam("id", userId)
        .when()
        .get("/user/{id}")
        .then()
        .statusCode(200)
        .body("username", equalTo(TEST_USERNAME));
  }

  @Test
  @Order(5)
  @DisplayName("Should update user profile successfully")
  public void testUpdateUser() {
    ModifyUserRequest request = new ModifyUserRequest();
    request.bio = "This is my updated bio";
    request.username = "updateduser";

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/user")
        .then()
        .statusCode(200)
        .body("bio", equalTo("This is my updated bio"))
        .body("username", equalTo("updateduser"));
  }

  @Test
  @Order(6)
  @DisplayName("Should upload profile image successfully")
  public void testUploadProfileImage() throws Exception {
    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();

    given()
        .contentType("multipart/form-data")
        .multiPart("file", testFile, "image/jpeg")
        .when()
        .put("/user/image")
        .then()
        .statusCode(200)
        .body("profileImage", notNullValue());
  }

  @Test
  @Order(7)
  @DisplayName("Should delete user successfully")
  public void testDeleteUser() {
    given().when().delete("/user").then().statusCode(204);

    given().pathParam("id", userId).when().get("/user/{id}").then().statusCode(404);
  }

  // Additional tests for error cases...
            .extract()
            .response();

    userId = response.jsonPath().getString("id");
  }

  @Test
  @Order(2)
  @DisplayName("Should login successfully")
  public void testLogin() {
    LoginRequest request = new LoginRequest();
    request.username = TEST_USERNAME;
    request.password = TEST_PASSWORD;

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/login")
        .then()
        .statusCode(200)
        .body("username", equalTo(TEST_USERNAME))
        .body("id", notNullValue());
  }

  @Test
  @Order(3)
  @DisplayName("Should get current user successfully")
  public void testGetCurrentUser() {
    given()
        .when()
        .get("/user")
        .then()
        .statusCode(200)
        .body("username", equalTo(TEST_USERNAME))
        .body("email", equalTo(TEST_EMAIL));
  }

  @Test
  @Order(4)
  @DisplayName("Should get user by ID successfully")
  public void testGetUserById() {
    given()
        .pathParam("id", userId)
        .when()
        .get("/user/{id}")
        .then()
        .statusCode(200)
        .body("username", equalTo(TEST_USERNAME));
  }

  @Test
  @Order(5)
  @DisplayName("Should update user profile successfully")
  public void testUpdateUser() {
    ModifyUserRequest request = new ModifyUserRequest();
    request.bio = "This is my updated bio";
    request.username = "updateduser";

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/user")
        .then()
        .statusCode(200)
        .body("bio", equalTo("This is my updated bio"))
        .body("username", equalTo("updateduser"));
  }

  @Test
  @Order(6)
  @DisplayName("Should upload profile image successfully")
  public void testUploadProfileImage() throws Exception {
    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();

    given()
        .contentType("multipart/form-data")
        .multiPart("file", testFile, "image/jpeg")
        .when()
        .put("/user/image")
        .then()
        .statusCode(200)
        .body("profileImage", notNullValue());
  }

  @Test
  @Order(7)
  @DisplayName("Should delete user successfully")
  public void testDeleteUser() {
    given().when().delete("/user").then().statusCode(204);

    given().pathParam("id", userId).when().get("/user/{id}").then().statusCode(404);
  }

  // Additional tests for error cases...
}
