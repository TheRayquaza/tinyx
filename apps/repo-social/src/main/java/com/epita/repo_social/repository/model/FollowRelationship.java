package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

public record FollowRelationship(UserNode source, UserNode target) {

  public FollowRelationship(final @NotNull UserNode source, final @NotNull UserNode target) {
    this.source = source;
    this.target = target;
  }

  public String findCypher() {
    String query =
        """
                Match (n1:User {userId:"%s"})-[f:HAS_FOLLOWED]->(n2:User {userId:"%s"})
                Return f
                """;
    return String.format(query, this.source.userId(), this.target.userId());
  }

  public String createCypher() {
    String query =
        """
                Match (n1:User {userId:"%s"}), (n2:User {userId:"%s"})
                Merge (n1)-[f:HAS_FOLLOWED]->(n2)
                Return f
                """;
    return String.format(query, this.source.userId(), this.target.userId());
  }

  public String deleteCypher() {
    String query =
        """
                Match (n1:User {userId:"%s"})-[f:HAS_FOLLOWED]->(n2:User {userId:"%s"})
                Delete f
                """;
    return String.format(query, this.source.userId(), this.target.userId());
  }
}
