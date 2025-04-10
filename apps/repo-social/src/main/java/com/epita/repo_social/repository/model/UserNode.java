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
                "MATCH (u:User{userId:\"%s\"}) RETURN u",
                this.userId
        );
    }

    public String createOrUpdateCypher() {
        return String.format(
                """
                Merge (u:User {userId: "%s"})
                On create set
                    u.username = "%s",
                    u.email = "%s",
                    u.bio = "%s",
                    u.profileImage = "%s",
                    u.createdAt = datetime("%s"),
                    u.updatedAt = datetime("%s"),
                    u.deleted = false
                On match set
                    u.username = "%s",
                    u.email = "%s",
                    u.bio = "%s",
                    u.profileImage = "%s",
                    u.updatedAt = datetime("%s")
                """,
                this.userId,
                this.username,
                this.email,
                this.bio,
                this.profileImage,
                this.createdAt,
                this.updatedAt,
                this.username,
                this.email,
                this.bio,
                this.profileImage,
                this.updatedAt
        );
    }

    public String deleteCypher() {
        return String.format(
                "MATCH (n:User {userId: \"%s\"}) SET n.deleted = true RETURN n",
                this.userId
        );
    }

    public String getLikedPostsCypher() {
        return String.format(
                "MATCH (n:User {userId:\"%s\"})-[:HAS_LIKED]->(p:Post) RETURN p",
                this.userId);
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

    public String deleteRelationshipCypher() {
        return String.format(
                "MATCH (:User {userId:\"%s\"})-[r]-() DELETE r",
                this.userId);
    }
}
