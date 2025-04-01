package com.epita.repo_social;

import static io.restassured.RestAssured.given;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.repo_social.controller.RepoSocialController;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

@QuarkusTest
@TestHTTPEndpoint(RepoSocialController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SocialControllerTest {

  @Inject AuthContext authContext;

  private static final String TEST_ID = "15a1a100c293c91129883571";
  private static final String TEST_USERNAME = "testuser";
  private static final String POST_ID = "15a1a100c293c91129883571";

  @Test
  @Order(1)
  @DisplayName("Should like a post")
  public void testLikePost() {
    String token = AuthService.generateToken(TEST_ID, TEST_USERNAME);
    AuthEntity authEntity = new AuthEntity(TEST_ID, TEST_USERNAME);
    authContext.setAuthEntity(authEntity);
    Response response =
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .post("/social/post/" + POST_ID + "/like")
            .then()
            .extract()
            .response();

    System.out.println(response.body().prettyPrint());

    response.then().statusCode(201);
  }
}
