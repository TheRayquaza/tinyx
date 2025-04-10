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
    @Order(8)
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
    @Order(8)
    void blockUserBlockedby() {
        // Tenter de bloquer un user en étant bloqué
        String token = AuthService.generateToken(USER_ID_2, USERNAME_2);
        AuthEntity authEntity = new AuthEntity(USER_ID_2, USERNAME_2);
        authContext.setAuthEntity(authEntity);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .post("/user/" + USER_ID_1 + "/block")
                        .then()
                        .extract()
                        .response();

        System.out.println(response.body().prettyPrint());
        response.then().statusCode(403).extract().response();
    }

    @Test
    @Order(9)
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
    @Order(10)
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
    @Order(11)
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
    @Order(12)
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
    @Order(13)
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
    @Order(14)
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
    @Order(15)
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
    @Order(16)
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
    @Order(17)
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
    @Order(18)
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
    @Order(19)
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
}
