package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;

public record BlockRelationship(UserNode source, UserNode target) {

  public BlockRelationship(final @NotNull UserNode source, final @NotNull UserNode target) {
    this.source = source;
    this.target = target;
  }

  public String findCypher() {
    String query =
        """
                Match (n1:User {userId:"%s"})-[b:HAS_BLOCKED]->(n2:User {userId:"%s"})
                Return b
                """;
    return String.format(query, this.source.userId(), this.target.userId());
  }

  public String createCypher() {
    String query =
        """
                Match (n1:User {userId:"%s"}), (n2:User {userId:"%s"})
                Merge (n1)-[b:HAS_BLOCKED]->(n2)
                Return b
                """;
    return String.format(query, this.source.userId(), this.target.userId());
  }

  public String deleteCypher() {
    String query =
        """
                Match (n1:User {userId:"%s"})-[b:HAS_BLOCKED]->(n2:User {userId:"%s"})
                Delete b
                """;
    return String.format(query, this.source.userId(), this.target.userId());
  }
}
