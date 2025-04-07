package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

public record LikeRelationship(UserNode source, PostNode target) {

    public LikeRelationship(final @NotNull UserNode source,
                            final @NotNull PostNode target) {
        this.source = source;
        this.target = target;
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
                Match (n1:User {userId:"%s"}), (n2:Post {postId:"%s"})
                Merge (n1)-[l:HAS_LIKED]->(n2)
                Return l
                """;
        return String.format(query, this.source.userId(), this.target.postId());
    }

    public String deleteCypher() {
        String query = """
                Match (n1:User {userId:"%s"})-[l:HAS_LIKED]->(n2:Post {postId:"%s"})
                Delete l
                """;
        return String.format(query, this.source.userId(), this.target.postId());
    }
}
