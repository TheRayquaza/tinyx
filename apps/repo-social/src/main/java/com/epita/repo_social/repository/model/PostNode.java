package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

public record PostNode(String postId, String ownerId, boolean deleted) {

    public PostNode(final @NotNull String postId) {
        this(postId, null, false);
    }

    public static PostNode from(final @NotNull org.neo4j.driver.types.Node neo4jNode) {
        return new PostNode(
                neo4jNode.get("postId").asString(),
                neo4jNode.get("ownerId").asString(),
                neo4jNode.get("deleted").asBoolean());
    }

    public String findCypher() {
        return String.format(
                "Match (p:Post {postId:\"%s\"}) Return p",
                this.postId);
    }

    public String createOrUpdateCypher() {
        return String.format(
                """
                Merge (p:Post {postId: "%s"})
                On create set
                    p.ownerId = "%s",
                    p.deleted = false,
                On match set
                    p.ownerId = "%s",
                """,
                this.postId,
                this.ownerId,
                this.ownerId);
    }

    public String deleteCypher() {
        return String.format(
                "MATCH (p:Post {postId:\"%s\"}) SET p.deleted = true",
                this.postId);
    }

    public String getLikesCypher() {
        return String.format(
                "MATCH (:Post {postId:\"%s\"})<-[:HAS_LIKED]-(u:User) RETURN u",
                this.postId);
    }

    public String deleteLikesCypher() {
        return String.format(
                "MATCH (:Post {postId:\"%s\"})<-[l:HAS_LIKED]-(:User) DELETE l",
                this.postId);
    }
}
