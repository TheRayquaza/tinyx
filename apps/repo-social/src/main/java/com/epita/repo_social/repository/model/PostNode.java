package com.epita.repo_social.repository.model;

import io.smallrye.common.constraint.NotNull;
import java.time.LocalDateTime;

public record PostNode(
    String postId,
    String ownerId,
    String text,
    String media,
    String repostId,
    String replyToPostId,
    Boolean isReply,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean deleted) {

  public PostNode(final @NotNull String postId) {
    this(postId, null, null, null, null, null, null, null, null, false);
  }

  public static PostNode from(final @NotNull org.neo4j.driver.types.Node neo4jNode) {
    return new PostNode(
        neo4jNode.get("postId").asString(),
        neo4jNode.get("ownerId").asString(),
        neo4jNode.get("text").asString(),
        neo4jNode.get("media").asString(),
        neo4jNode.get("repostId").asString(),
        neo4jNode.get("replyToPostId").asString(),
        neo4jNode.get("isReply").asBoolean(),
        neo4jNode.get("createdAt").asZonedDateTime().toLocalDateTime(),
        neo4jNode.get("updatedAt").asZonedDateTime().toLocalDateTime(),
        neo4jNode.get("deleted").asBoolean());
  }

  public String findCypher() {
    return String.format("Match (p:Post {postId:\"%s\"}) Return p", this.postId);
  }

  public String createOrUpdateCypher() {
    return String.format(
        """
                        Merge (p:Post {postId: "%s"})
                        On create set
                            p.ownerId = "%s",
                            p.text = "%s",
                            p.media = "%s",
                            p.repostId = "%s",
                            p.replyToPostId = "%s",
                            p.isReply = %s,
                            p.createdAt = datetime("%s"),
                            p.updatedAt = datetime("%s"),
                            p.deleted = false,
                        On match set
                            p.ownerId = "%s",
                            p.text = "%s",
                            p.media = "%s",
                            p.repostId = "%s",
                            p.replyToPostId = "%s",
                            p.isReply = %s,
                            p.updatedAt = datetime("%s")
                        """,
        this.postId,
        this.ownerId,
        this.text,
        this.media,
        this.repostId,
        this.replyToPostId,
        this.isReply,
        this.createdAt,
        this.updatedAt,
        this.ownerId,
        this.text,
        this.media,
        this.repostId,
        this.replyToPostId);
  }

  public String deleteCypher() {
    return String.format("MATCH (p:Post {postId:\"%s\"}) SET p.deleted = true", this.postId);
  }

  public String getLikesCypher() {
    return String.format(
        "MATCH (:Post {postId:\"%s\"})<-[:HAS_LIKED]-(u:User) RETURN u", this.postId);
  }

  public String deleteLikesCypher() {
    return String.format(
        "MATCH (:Post {postId:\"%s\"})<-[l:HAS_LIKED]-(:User) DELETE l", this.postId);
  }
}
