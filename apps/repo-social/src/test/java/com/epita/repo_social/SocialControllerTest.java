package com.epita.repo_social;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.not;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.auth.service.entity.AuthEntity;
import com.epita.repo_social.controller.RepoSocialController;
import com.epita.repo_social.repository.Neo4jRepository;
import io.quarkus.arc.Arc;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestHTTPEndpoint(RepoSocialController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestSecurity(authorizationEnabled = false)
class RepoSocialControllerTest {
    @Inject
    AuthContext authContext;
    @Inject
    Neo4jRepository neo4jRepository;

    private static final String USER_ID_1 = "user-1";
    private static final String USER_ID_2 = "user-2";
    private static final String USER_ID_3 = "user-3";
    private static final String USER_ID_4 = "user-4";

    private static final String USERNAME_1 = "alice";
    private static final String USERNAME_2 = "bob";
    private static final String USERNAME_3 = "carol";
    private static final String USERNAME_4 = "dave";

    private static final String POST_ID_1 = "post-1";
    private static final String POST_ID_2 = "post-2";

    @BeforeAll
    static void setup() {
        Neo4jRepository repository = Arc.container().instance(Neo4jRepository.class).get();

        // Supprimer toutes les données existantes
        repository.createNode("MATCH (n) DETACH DELETE n");

        // Créer les utilisateurs
        repository.createNode(
                "CREATE (u1:User {" +
                        "  userId: \"user-1\"," +
                        "  username: \"alice\"," +
                        "  email: \"alice@example.com\"," +
                        "  bio: \"Bio of Alice\"," +
                        "  profileImage: \"alice.jpg\"," +
                        "  createdAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  updatedAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  deleted: false" +
                        "})" +
                        "CREATE (u2:User {" +
                        "  userId: \"user-2\"," +
                        "  username: \"bob\"," +
                        "  email: \"bob@example.com\"," +
                        "  bio: \"Bio of Bob\"," +
                        "  profileImage: \"bob.jpg\"," +
                        "  createdAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  updatedAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  deleted: false" +
                        "})" +
                        "CREATE (u3:User {" +
                        "  userId: \"user-3\"," +
                        "  username: \"carol\"," +
                        "  email: \"carol@example.com\"," +
                        "  bio: \"Bio of Carol\"," +
                        "  profileImage: \"carol.jpg\"," +
                        "  createdAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  updatedAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  deleted: false" +
                        "})" +
                        "CREATE (u4:User {" +
                        "  userId: \"user-4\"," +
                        "  username: \"dave\"," +
                        "  email: \"dave@example.com\"," +
                        "  bio: \"Bio of Dave\"," +
                        "  profileImage: \"dave.jpg\"," +
                        "  createdAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  updatedAt: datetime(\"2024-04-01T10:00:00\")," +
                        "  deleted: false" +
                        "})"
        );

        // Créer les posts
        repository.createNode(
                "CREATE (p1:Post {" +
                        "  postId: \"post-1\"," +
                        "  ownerId: \"user-1\"" +
                        "})" +
                        "CREATE (p2:Post {" +
                        "  postId: \"post-2\"," +
                        "  ownerId: \"user-2\"" +
                        "})"
        );
    }

    // LIKE AND UNLIKE TESTS

    @Test
    @Order(1)
    void likePost() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(201).extract().response();
    }

    @Test
    @Order(2)
    void unlikePost() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(204).extract().response();

        // Vérifier que la relation a bien été supprimée
        boolean relationExists = neo4jRepository.checkRelationExists(
                "MATCH (u:User {userId: \"" + USER_ID_1 + "\"})-[r:LIKES]->(p:Post {postId: \"" + POST_ID_2 + "\"}) RETURN r"
        );
        assert !relationExists;
    }

    // FOLLOW AND UNFOLLOW TESTS

    @Test
    @Order(3)
    void followUser() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_2 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(201).extract().response();
    }

    @Test
    @Order(4)
    void unfollowUser() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + USER_ID_2 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(204).extract().response();
    }


    // BLOCK AND UNBLOCK TESTS

    @Test
    @Order(5)
    void blockUser() {

        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_2 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(201).extract().response();

    }

    @Test
    @Order(6)
    void likePostBlocked() {
        // Tenter de liker un post d'un user bloqué
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(7)
    void likePostBlockedby() {
        // Tenter de liker un post en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/post/" + POST_ID_1 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(8)
    void followUserBlocked() {
        // Tenter de suivre un user bloqué
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_2 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(9)
    void followUserBlockedby() {
        // Tenter de suivre un user en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_1 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(10)
    void getPostLikesOfBlockedUser() {
        // Tenter de récupérer les likes d'un post d'un user bloqué}
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(11)
    void getPostLikesBlockedByUser() {
        // Tenter de récupérer les likes d'un post en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/post/" + POST_ID_1 + "/like")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(12)
    void getFollowersOfBlockedUser() {
        // Tenter de récupérer les followers d'un user bloqué
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/follower")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(13)
    void getFollowersBlockedByUser() {
        // Tenter de récupérer les followers d'un user en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_1 + "/follower")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(14)
    void getFollowingOfBlockedUser() {
        // Tenter de récupérer les following d'un user bloqué
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/following")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(15)
    void getFollowingBlockedByUser() {
        // Tenter de récupérer les following d'un user en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_1 + "/following")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(16)
    void getBlockedOfBlockedUsers() {
        // Tenter de récupérer les blocked d'un user bloqué
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/blocked")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(17)
    void getBlockedByBlockedUser() {
        // Tenter de récupérer les blocked d'un user en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_1 + "/blocked")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(18)
    void getBlockedByOfBlockedUser() {
        // Tenter de récupérer les blockedBy d'un user bloqué
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/blockedBy")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(19)
    void getBlockedByOfBlockedByUser() {
        // Tenter de récupérer les blockedBy d'un user en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_1 + "/blockedBy")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }


    @Test
    @Order(20)
    void unblockUser() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + USER_ID_2 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(204).extract().response();

        // Vérifier que la relation a bien été supprimée
        boolean relationExists = neo4jRepository.checkRelationExists(
                "MATCH (u1:User {userId: \"" + USER_ID_1 + "\"})-[r:BLOCKS]->(u2:User {userId: \"" + USER_ID_2 + "\"}) RETURN r"
        );
        assert !relationExists;
    }

    @Test
    @Order(21)
    void getUserFollowersEmpty() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/follower")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("followers", notNullValue())
                .body("followers", hasSize(0))
                .extract()
                .response();
    }

    @Test
    @Order(22)
    void getUserFollowingEmpty() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/following")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("following", notNullValue())
                .body("following", hasSize(0))
                .extract()
                .response();
    }

    @Test
    @Order(23)
    void getUserBlockedEmpty() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/blocked")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("blocked", notNullValue())
                .body("blocked", hasSize(0))
                .extract()
                .response();
    }

    @Test
    @Order(24)
    void getUserBlockedByEmpty() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/blockedBy")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("blockedBy", notNullValue())
                .body("blockedBy", hasSize(0))
                .extract()
                .response();
    }


    @Test
    @Order(25)
    void likePostAlreadyLiked() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response responseFirstLike =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseFirstLike.body().prettyPrint());
        responseFirstLike.then().statusCode(201).extract().response();

        Response responseSecondLike =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();
        System.out.println(responseSecondLike.body().prettyPrint());
        responseSecondLike.then().statusCode(201).extract().response();
    }

    @Test
    @Order(26)
    void unlikePostNotLiked() {
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/post/" + POST_ID_1 + "/like")
                        .then()
                        .extract()
                        .response();
        System.out.println(response.body().prettyPrint());
        response.then().statusCode(204).extract().response();
    }

    @Test
    @Order(27)
    void followUserAlreadyFollowed() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response responseFirstFollow =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_2 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseFirstFollow.body().prettyPrint());
        responseFirstFollow.then().statusCode(201).extract().response();

        Response responseSecondFollow =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_2 + "/follow")
                        .then()
                        .extract()
                        .response();
        System.out.println(responseSecondFollow.body().prettyPrint());
        responseSecondFollow.then().statusCode(201).extract().response();
    }

    @Test
    @Order(28)
    void unfollowUserNotFollowed() {
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + USER_ID_1 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(204).extract().response();
    }

    @Test
    @Order(29)
    void blockUserAlreadyBlocked() {
        String token = AuthService.generateToken(USER_ID_3, USERNAME_3);
        AuthEntity authEntity = new AuthEntity(USER_ID_3, USERNAME_3);
        authContext.setAuthEntity(authEntity);

        Response responseFirstBlock =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_4 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseFirstBlock.body().prettyPrint());
        responseFirstBlock.then().statusCode(201).extract().response();

        Response responseSecondBlock =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_4 + "/block")
                        .then()
                        .extract()
                        .response();
        System.out.println(responseSecondBlock.body().prettyPrint());
        responseSecondBlock.then().statusCode(201).extract().response();
    }

    @Test
    @Order(30)
    void unblockUserNotBlocked() {
        String token = AuthService.generateToken(USER_ID_4, USERNAME_4);
        AuthEntity authEntity = new AuthEntity(USER_ID_4, USERNAME_4);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + USER_ID_3 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(204).extract().response();
    }

    @Test
    @Order(31)
    void getPostLikesOne() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/post/" + POST_ID_2 + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        //verify if the response is a list of users of size one with the id of user-1
        response
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(USER_ID_1))
                .extract()
                .response();
    }

    @Test
    @Order(32)
    void getUserFollowersOne() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/follower")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(USER_ID_1))
                .extract()
                .response();
    }

    @Test
    @Order(33)
    void getUserFollowingOne() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_1 + "/following")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(USER_ID_2))
                .extract()
                .response();
    }

    @Test
    @Order(34)
    void getUserBlockedOne() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_3 + "/blocked")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(USER_ID_4))
                .extract()
                .response();
    }

    @Test
    @Order(35)
    void getUserBlockedByOne() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_4 + "/blockedBy")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(USER_ID_3))
                .extract()
                .response();
    }

    // test every endpoints with unexisting post / user id
    @Test
    @Order(36)
    void likePostUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/post/" + "unexisting" + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(37)
    void unlikePostUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/post/" + "unexisting" + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(38)
    void followUserUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + "unexisting" + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(39)
    void unfollowUserUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + "unexisting" + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(40)
    void blockUserUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + "unexisting" + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(41)
    void unblockUserUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + "unexisting" + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(42)
    void getPostLikesUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/post/" + "unexisting" + "/like")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(43)
    void getUserFollowersUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + "unexisting" + "/follower")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(44)
    void getUserFollowingUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + "unexisting" + "/following")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(45)
    void getUserBlockedUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + "unexisting" + "/blocked")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(46)
    void getUserBlockedByUnexisting() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + "unexisting" + "/blockedBy")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(404).extract().response();
    }

    @Test
    @Order(47)
    void blockingDeletesFollowRelationship() {
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);

        // follow user 1
        Response responseFollow =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_1 + "/follow")
                        .then()
                        .extract()
                        .response();

        Response responseBlock =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_1 + "/block")
                        .then()
                        .extract()
                        .response();

        //get the followers and following of user 2
        Response responseFollower =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/follower")
                        .then()
                        .extract()
                        .response();

        responseFollower
                .then()
                .statusCode(200)
                .body("size()", is(0))
                .extract()
                .response();

        Response responseFollowing =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/user/" + USER_ID_2 + "/following")
                        .then()
                        .extract()
                        .response();
        responseFollowing
                .then()
                .statusCode(200)
                .body("size()", is(0))
                .extract()
                .response();
    }

    @Test
    @Order(48)
    void cannotFollowOrUnfollowYourself() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        // Tenter de suivre soi-même
        Response responseFollow =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_1 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseFollow.body().prettyPrint());
        responseFollow.then().statusCode(400).extract().response();

        // Tenter de ne plus suivre soi-même
        Response responseUnfollow =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + USER_ID_1 + "/follow")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseUnfollow.body().prettyPrint());
        responseUnfollow.then().statusCode(400).extract().response();
    }

    @Test
    @Order(49)
    void cannotBlockOrUnblockYourself() {
        String token = AuthService.generateToken(USER_ID_1, USERNAME_1);
        AuthEntity authEntity = new AuthEntity(USER_ID_1, USERNAME_1);
        authContext.setAuthEntity(authEntity);

        // Tenter de bloquer soi-même
        Response responseBlock =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_1 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseBlock.body().prettyPrint());
        responseBlock.then().statusCode(400).extract().response();

        // Tenter de ne plus bloquer soi-même
        Response responseUnblock =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/user/" + USER_ID_1 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(responseUnblock.body().prettyPrint());
        responseUnblock.then().statusCode(400).extract().response();
    }
}
