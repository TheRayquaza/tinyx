package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

public record PostNode(String postId, String ownerId) {

    public PostNode(final @NotNull String postId) {
        this(postId, null);
    }

    public static PostNode from(final @NotNull org.neo4j.driver.types.Node neo4jNode) {
        return new PostNode(
                neo4jNode.get("postId").asString(),
                neo4jNode.get("ownerId").asString());
    }

    public String findCypher() {
        return String.format(
                "Match (p:Post {postId:\"%s\") Return p",
                this.postId);
    }

    public String createCypher() {
        return String.format(
                "Create (p:Post {postId: \"%s\"}) Return p",
                this.postId);
    }

    public String getLikesCypher() {
        return String.format(
                "MATCH (:Post {postId:\"%s\"})<-[:HAS_LIKED]-(u:User) RETURN u",
                this.postId);
    }
}
