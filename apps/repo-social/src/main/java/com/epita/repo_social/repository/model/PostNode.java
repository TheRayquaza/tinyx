package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

public record PostNode(String postId) {

    public PostNode(final @NotNull String postId) {
        this.postId = postId;
    }

    public static UserNode from(final @NotNull org.neo4j.driver.types.Node neo4jNode) {
        final var postId = neo4jNode.get("postId").asString();

        return new UserNode(postId);
    }

    public String findCypher() {
        return String.format(
                "Match (n:User {postId:\"%s\") Return n",
                this.postId);
    }

    public String createCypher() {
        return String.format(
                "Create (n:User {postId: \"%s\"}) Return n",
                this.postId);
    }

    public String getLikesCypher() {
        return String.format(
                "MATCH (n:Post {postId:\"%s\"})<-[:HAS_LIKED]-(u:User) RETURN u",
                this.postId);
    }
}
