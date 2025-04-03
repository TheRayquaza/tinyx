package com.epita.repo_social.service;

import static org.neo4j.driver.Values.parameters;

import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.FollowCommand;
import com.epita.exchange.redis.command.LikeCommand;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.exchange.s3.service.S3Service;
import com.epita.exchange.utils.Logger;
import com.epita.repo_social.RepoSocialErrorCode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.neo4j.driver.Driver;

@ApplicationScoped
public class SocialService implements Logger {

  @Inject AuthService authService;

  @Inject S3Service s3Service;

  @Inject RedisPublisher redisPublisher;

  @Inject Driver neo4jDriver;

  @ConfigProperty(name = "repo.social.like.command.channel")
  @Inject
  String likeCommandChannel;

  @ConfigProperty(name = "repo.social.block.command.channel")
  @Inject
  String blockCommandChannel;

  @ConfigProperty(name = "repo.social.follow.command.channel")
  @Inject
  String followCommandChannel;

  public void likePost(String postId) {
    // Update Neo4j
    final var session = neo4jDriver.session();
    // socialRepository...
    final var cypher =
        "MATCH (u:UserModel {user_id: $userId}), (p:PostModel {post_id: $postId})\n"
            + "MERGE (u)-[:LIKES]->(p);\n";
    logger().info("Running like cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("postId: {}\n", postId);

    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "postId", postId))
                .consume()
                .counters()
                .containsUpdates()))
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(postId);
    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        new LikeCommand(UUID.randomUUID(), authService.getUserId(), postId, true));
  }

  public void unlikePost(String postId) {
    // Update Neo4j
    final var session = neo4jDriver.session();
    // socialRepository...
    final var cypher =
        "MATCH (u:UserModel {user_id: $userId})-[r:LIKES]->(p:PostModel {post_id: $postId})\n"
            + "DELETE r;\n";
    logger().info("Running unlike cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("postId: {}\n", postId);

    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "postId", postId))
                .consume()
                .counters()
                .containsUpdates()))
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(postId);
    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        new LikeCommand(UUID.randomUUID(), authService.getUserId(), postId, false));
  }

  public void followUser(String userId) {
    // Update Neo4jUserModel
    final var session = neo4jDriver.session();
    // socialRepository...
    final var cypher =
        "MATCH (u1:UserModel {user_id: $userId}), (u2:UserModel {user_id: $userIdFollowed})\n"
            + "MERGE (u1)-[:FOLLOWS]->(u2);\n";
    logger().info("Running follow cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userIdFollowed: {}\n", userId);

    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "userIdFollowed", userId))
                .consume()
                .counters()
                .containsUpdates()))
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(userId);
    // Redis
    redisPublisher.publish(
        followCommandChannel,
        new FollowCommand(UUID.randomUUID(), authService.getUserId(), userId, true));
  }

  public void unfollowUser(String userId) {
    // Update Neo4jUserModel
    final var session = neo4jDriver.session();
    // socialRepository...
    final var cypher =
        "MATCH (u1:UserModel {user_id: $userId})-[r:FOLLOWS]->(u2:UserModel {user_id: $userIdFollowed})\n"
            + "DELETE r;\n";
    logger().info("Running unfollow cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userIdFollowed: {}\n", userId);

    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "userIdFollowed", userId))
                .consume()
                .counters()
                .containsUpdates()))
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(userId);
    // Redis
    redisPublisher.publish(
        followCommandChannel,
        new FollowCommand(UUID.randomUUID(), authService.getUserId(), userId, false));
  }

  public void blockUser(String userId) {
    // Update Neo4jUserModel
    final var session = neo4jDriver.session();
    // socialRepository...
    final var cypher =
        "MATCH (u1:UserModel {user_id: $userId}), (u2:UserModel {user_id: $userIdBlocked})\n"
            + "MERGE (u1)-[:BLOCKS]->(u2);\n";
    logger().info("Running block cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userIdBlockeded: {}\n", userId);

    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "userIdBlocked", userId))
                .consume()
                .counters()
                .containsUpdates()))
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(userId);
    // Redis
    redisPublisher.publish(
        blockCommandChannel,
        new BlockCommand(UUID.randomUUID(), authService.getUserId(), userId, true));
  }

  public void unblockUser(String userId) {
    // Update Neo4jUserModel
    final var session = neo4jDriver.session();
    // socialRepository...

    final var cypher =
        "MATCH (u1:UserModel {user_id: $userId})-[r:BLOCKS]->(u2:UserModel {user_id: $userIdBlocked})\n"
            + "DELETE r\n";
    logger().info("Running unblock cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userIdBlockeded: {}\n", userId);

    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "userIdBlocked", userId))
                .consume()
                .counters()
                .containsUpdates()))
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(userId);
    // Redis
    redisPublisher.publish(
        blockCommandChannel,
        new BlockCommand(UUID.randomUUID(), authService.getUserId(), userId, false));
  }

  /*
  public List<userId> getUsersLike (String postId) {
    final var session = neo4jDriver.session();

    final var cypher = "MATCH (u:UserModel)-[:LIKES]->(p:PostModel {post_id: $postId})\n})"
          + "RETURN u.user_id;";
  }
  */

}
