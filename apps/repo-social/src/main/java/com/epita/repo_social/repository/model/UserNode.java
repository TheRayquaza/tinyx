package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;
import lombok.NonNull;
import java.time.LocalDateTime;

public record UserNode(
        @NonNull String userId,
        String username,
        String email,
        String bio,
        String profileImage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean deleted
) {

    // Single argument constructor
    public UserNode(final @NotNull String userId) {
        this(userId, null, null, null, null, null, null, false);
    }

    public static UserNode from(final @NotNull org.neo4j.driver.types.Node neo4jNode) {
        return new UserNode(
                neo4jNode.get("userId").asString(),
                neo4jNode.get("username").asString(),
                neo4jNode.get("email").asString(),
                neo4jNode.get("bio").asString(),
                neo4jNode.get("profileImage").asString(),
                neo4jNode.get("createdAt").asZonedDateTime().toLocalDateTime(),
                neo4jNode.get("updatedAt").asZonedDateTime().toLocalDateTime(),
                neo4jNode.get("deleted").asBoolean()
        );
    }

    public String findCypher() {
        return String.format(
                "MATCH (n:User{userId:\"%s\"}) RETURN n",
                this.userId
        );
    }

    public String createCypher() {
        return String.format(
                "CREATE (n:User {userId: \"%s\"%s%s%s%s%s%s%s}) RETURN n",
                this.userId,
                username != null ? String.format(", username: \"%s\"", username) : "",
                email != null ? String.format(", email: \"%s\"", email) : "",
                bio != null ? String.format(", bio: \"%s\"", bio) : "",
                profileImage != null ? String.format(", profileImage: \"%s\"", profileImage) : "",
                createdAt != null ? String.format(", createdAt: \"%s\"", createdAt) : "",
                updatedAt != null ? String.format(", updatedAt: \"%s\"", updatedAt) : "",
                deleted ? ", deleted: true" : ""
        );
    }

    public String deleteCypher() {
        return String.format(
                "MATCH (n:User {userId: \"%s\"}) SET n.deleted = true RETURN n",
                this.userId
        );
    }

    public String getFollowersCypher() {
        return String.format(
                "MATCH (n:User {userId:\"%s\"})<-[:HAS_FOLLOWED]-(u:User) RETURN u",
                this.userId);
    }

    public String getFollowingsCypher() {
        return String.format(
                "MATCH (n:User {userId:\"%s\"})-[:HAS_FOLLOWED]->(u:User) RETURN u",
                this.userId);
    }

    public String getBlockedCypher() {
        return String.format(
                "MATCH (:User {userId:\"%s\"})-[:HAS_BLOCKED]->(u:User) RETURN u",
                this.userId);
    }

    public String getBlockedByCypher() {
        return String.format(
                "MATCH (n:User {userId:\"%s\"})<-[:HAS_BLOCKED]-(u:User) RETURN u",
                this.userId);
    }

    public String deleteLikesOfUserPostsCypher(String blockedUserId) {
        return String.format(
                "MATCH (:User {userId:\"%s\"})-[l:HAS_LIKED]->(:Post {ownerId:\"%s\"}) DELETE l",
                this.userId,
                blockedUserId);
    }
}
