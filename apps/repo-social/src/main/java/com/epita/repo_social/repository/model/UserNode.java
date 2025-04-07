package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
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
                neo4jNode.containsKey("username") ? neo4jNode.get("username").asString() : null,
                neo4jNode.containsKey("email") ? neo4jNode.get("email").asString() : null,
                neo4jNode.containsKey("bio") ? neo4jNode.get("bio").asString() : null,
                neo4jNode.containsKey("profileImage") ? neo4jNode.get("profileImage").asString() : null,
                neo4jNode.containsKey("createdAt") ? LocalDateTime.parse(neo4jNode.get("createdAt").asString()) : null,
                neo4jNode.containsKey("updatedAt") ? LocalDateTime.parse(neo4jNode.get("updatedAt").asString()) : null,
                neo4jNode.containsKey("deleted") && neo4jNode.get("deleted").asBoolean()
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
}
