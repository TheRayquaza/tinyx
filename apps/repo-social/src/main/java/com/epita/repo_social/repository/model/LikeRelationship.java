package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

import java.time.LocalDateTime;

public record LikeRelationship(UserNode source, PostNode target, LocalDateTime createdAt) {

    public LikeRelationship(final @NotNull UserNode source,
                            final @NotNull PostNode target) {
        this(source, target, LocalDateTime.now());
    }

    public String findCypher() {
        String query = """
                Match (n1:User {userId:"%s"})-[l:HAS_LIKED]->(n2:Post {postId:"%s"})
                Return l
                """;
        return String.format(query, this.source.userId(), this.target.postId());
    }

    public String createCypher() {
        String query = """
        MATCH (n1:User {userId: "%s"}), (n2:Post {postId: "%s"})
        MERGE (n1)-[l:HAS_LIKED]->(n2)
        ON CREATE SET l.createdAt = datetime("%s")
        RETURN l
        """;
        return String.format(query, this.source.userId(), this.target.postId(), this.createdAt.toString());
    }

    public String deleteCypher() {
        String query = """
                Match (n1:User {userId:"%s"})-[l:HAS_LIKED]->(n2:Post {postId:"%s"})
                Delete l
                """;
        return String.format(query, this.source.userId(), this.target.postId());
    }
}
