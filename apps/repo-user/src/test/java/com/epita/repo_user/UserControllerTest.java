package com.epita.repo_user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.repo_user.controller.RepoUserController;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import java.io.File;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestHTTPEndpoint(RepoUserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestSecurity(authorizationEnabled = false)
public class UserControllerTest {

  @Inject AuthContext authContext;

  private static String userId;
  private static final String TEST_USERNAME = "testuser";
  private static final String TEST_ID = "15a1a100c293c91129883571";
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
            .statusCode(200)
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
    String token = AuthService.generateToken(userId, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(userId, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    given()
        .header("Authorization", "Bearer " + token)
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
    System.out.println("User ID: " + userId);
    String token = AuthService.generateToken(userId, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(userId, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    given()
        .header("Authorization", "Bearer " + token)
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
    String token = AuthService.generateToken(userId, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(userId, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    ModifyUserRequest request = new ModifyUserRequest();
    request.bio = "This is my updated bio";
    request.username = TEST_USERNAME + "updated";

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .body(request)
        .when()
        .put("/user")
        .then()
        .statusCode(200)
        .body("bio", equalTo("This is my updated bio"))
        .body("username", equalTo(TEST_USERNAME + "updated"));
  }

  @Test
  @Order(6)
  @DisplayName("Should upload profile image successfully")
  public void testUploadProfileImage() throws Exception {
    String token = AuthService.generateToken(userId, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(userId, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    File testFile = File.createTempFile("profile", ".jpg");
    testFile.deleteOnExit();

    given()
        .header("Authorization", "Bearer " + token)
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
    String token = AuthService.generateToken(userId, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(userId, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .delete("/user")
        .then()
        .statusCode(204);

    // Verify that the user is deleted
    given()
        .header("Authorization", "Bearer " + token)
        .pathParam("id", userId)
        .when()
        .get("/user/{id}")
        .then()
        .statusCode(404);
  }

  // Additional tests for error cases...
}
