package com.epita.repo_social.service;

import static org.neo4j.driver.Values.parameters;

import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.FollowCommand;
import com.epita.exchange.redis.command.LikeCommand;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.exchange.utils.Logger;
import com.epita.repo_social.converter.UserNodeToUserEntity;
import com.epita.repo_social.repository.Neo4jRepository;
import com.epita.repo_social.repository.model.*;
import com.epita.repo_social.service.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SocialService implements Logger {

  @Inject AuthService authService;

  @Inject
  Neo4jRepository neo4jRepository;

  @Inject
  RedisPublisher redisPublisher;

  @Inject
  UserNodeToUserEntity userNodeToUserEntity;

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
    logger().info("Running like cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("postId: {}\n", postId);

    UserNode userNode = new UserNode(authService.getUserId());
    PostNode postNode = new PostNode(postId);

    LikeRelationship likeRelationship = new LikeRelationship(userNode, postNode);

    neo4jRepository.createRelation(likeRelationship.createCypher());

    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        new LikeCommand(UUID.randomUUID(), authService.getUserId(), postId, true));
  }

  public void unlikePost(String postId) {
    logger().info("Running unlike cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("postId: {}\n", postId);

    UserNode userNode = new UserNode(authService.getUserId());
    PostNode postNode = new PostNode(postId);

    LikeRelationship likeRelationship = new LikeRelationship(userNode, postNode);

    neo4jRepository.deleteRelation(likeRelationship.deleteCypher());

    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        new LikeCommand(UUID.randomUUID(), authService.getUserId(), postId, false));
  }

  public void followUser(String userId) {
    logger().info("Running follow cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userId Followed: {}\n", userId);

    UserNode followerNode = new UserNode(authService.getUserId());
    UserNode followedNode = new UserNode(userId);

    FollowRelationship followRelationship = new FollowRelationship(followerNode, followedNode);

    neo4jRepository.createRelation(followRelationship.createCypher());

    // Redis
    redisPublisher.publish(
        followCommandChannel,
        new FollowCommand(UUID.randomUUID(), authService.getUserId(), userId, true));
  }

  public void unfollowUser(String userId) {
    logger().info("Running unfollow cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userId Followed: {}\n", userId);

    UserNode followerNode = new UserNode(authService.getUserId());
    UserNode followedNode = new UserNode(userId);

    FollowRelationship followRelationship = new FollowRelationship(followerNode, followedNode);

    neo4jRepository.deleteRelation(followRelationship.deleteCypher());

    // Redis
    redisPublisher.publish(
        followCommandChannel,
        new FollowCommand(UUID.randomUUID(), authService.getUserId(), userId, false));
  }

  public void blockUser(String userId) {
    logger().info("Running block cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userId Blocked: {}\n", userId);

    UserNode blockerNode = new UserNode(authService.getUserId());
    UserNode blockedNode = new UserNode(userId);

    BlockRelationship blockRelationship = new BlockRelationship(blockerNode, blockedNode);

    neo4jRepository.createRelation(blockRelationship.createCypher());

    // Redis
    redisPublisher.publish(
        blockCommandChannel,
        new BlockCommand(UUID.randomUUID(), authService.getUserId(), userId, true));
  }

  public void unblockUser(String userId) {
    logger().info("Running unblock cypher script with : \n userId: {}\n", authService.getUserId());
    logger().info("userId Blocked: {}\n", userId);

    UserNode blockerNode = new UserNode(authService.getUserId());
    UserNode blockedNode = new UserNode(userId);

    BlockRelationship blockRelationship = new BlockRelationship(blockerNode, blockedNode);

    neo4jRepository.deleteRelation(blockRelationship.deleteCypher());

    // Redis
    redisPublisher.publish(
        blockCommandChannel,
        new BlockCommand(UUID.randomUUID(), authService.getUserId(), userId, false));
  }

  public List<UserEntity> getPostLikes(String postId) {
    logger().info("Running getPostLikes cypher script with : \n postId: {}\n", postId);

    PostNode postNode = new PostNode(postId);
    return neo4jRepository.getUsers(postNode.getLikesCypher()).stream()
            .map(userNodeToUserEntity::convertNotNull)
            .toList();
  }

  public List<UserEntity> getUserFollowers(String userId) {
    logger().info("Running getUserFollowers cypher script with : \n userId: {}\n", userId);

    UserNode userNode = new UserNode(userId);
    return neo4jRepository.getUsers(userNode.getFollowersCypher()).stream()
            .map(userNodeToUserEntity::convertNotNull)
            .toList();
  }

    public List<UserEntity> getUserFollowings(String userId) {
        logger().info("Running getUserFollowings cypher script with : \n userId: {}\n", userId);

        UserNode userNode = new UserNode(userId);
        return neo4jRepository.getUsers(userNode.getFollowingsCypher()).stream()
                .map(userNodeToUserEntity::convertNotNull)
                .toList();
    }

    public List<UserEntity> getUserBlocks(String userId) {
        logger().info("Running getUserBlocks cypher script with : \n userId: {}\n", userId);

        UserNode userNode = new UserNode(userId);
        return neo4jRepository.getUsers(userNode.getBlocksCypher()).stream()
                .map(userNodeToUserEntity::convertNotNull)
                .toList();
    }

}
