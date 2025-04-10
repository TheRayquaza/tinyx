package com.epita.repo_social.service;

import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.FollowCommand;
import com.epita.exchange.redis.command.LikeCommand;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.exchange.utils.Logger;
import com.epita.repo_social.RepoSocialErrorCode;
import com.epita.repo_social.converter.PostNodeToPostEntity;
import com.epita.repo_social.converter.UserNodeToUserEntity;
import com.epita.repo_social.repository.Neo4jRepository;
import com.epita.repo_social.repository.model.*;
import com.epita.repo_social.service.entity.PostEntity;
import com.epita.repo_social.service.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SocialService implements Logger {

  @Inject AuthService authService;

  @Inject Neo4jRepository neo4jRepository;

  @Inject RedisPublisher redisPublisher;

  @Inject UserNodeToUserEntity userNodeToUserEntity;

  @ConfigProperty(name = "repo.social.like.command.channel")
  @Inject
  String likeCommandChannel;

  @ConfigProperty(name = "repo.social.block.command.channel")
  @Inject
  String blockCommandChannel;

  @ConfigProperty(name = "repo.social.follow.command.channel")
  @Inject
  String followCommandChannel;

  @Inject PostNodeToPostEntity postNodeToPostEntity;

  private boolean isUserBlocked(UserNode source, UserNode target) {
    BlockRelationship blockRelationship = new BlockRelationship(source, target);
    return neo4jRepository.checkRelationExists(blockRelationship.findCypher());
  }

  private boolean isUserBlockedBy(UserNode source, UserNode target) {
    BlockRelationship blockRelationship = new BlockRelationship(target, source);
    return neo4jRepository.checkRelationExists(blockRelationship.findCypher());
  }

  private void throwErrorIfBlockRelationshipExists(
      UserNode source, UserNode target, String action) {
    // cannot follow if the user is blocked
    if (isUserBlocked(source, target)) {
      logger().info("User {} blocked the user {}", source.userId(), target.userId());
      throw RepoSocialErrorCode.FORBIDDEN.createError(
          "User {} blocked the user {} - Cannot {} user {}",
          source.userId(),
          target.userId(),
          action,
          target.userId());
    }
    // cannot follow if the user is blocked by
    if (isUserBlockedBy(source, target)) {
      logger().info("User {} is blocked by user {}", source.userId(), target.userId());
      throw RepoSocialErrorCode.FORBIDDEN.createError(
          "User {} is blocked by the User {} - Cannot {} user {}",
          authService.getUserId(),
          target.userId(),
          action,
          target.userId());
    }
  }

  public void createOrUpdatePostFromAggregate(PostAggregate postAggregate) {
    PostNode postNode =
        new PostNode(
            postAggregate.getId(),
            postAggregate.getOwnerId(),
            postAggregate.getText(),
            postAggregate.getMedia(),
            postAggregate.getRepostId(),
            postAggregate.getReplyToPostId(),
            postAggregate.isReply(),
            postAggregate.getCreatedAt(),
            postAggregate.getUpdatedAt(),
            postAggregate.isDeleted());
    if (postNode.deleted()) {
      neo4jRepository.deleteNode(postNode.deleteCypher());
      // delete all likes of the node
      neo4jRepository.deleteRelation(postNode.deleteLikesCypher());
    } else {
      neo4jRepository.createOrUpdateNode(postNode.createOrUpdateCypher());
    }
  }

  public void createOrUpdateUserFromAggregate(UserAggregate userAggregate) {
    UserNode userNode =
        new UserNode(
            userAggregate.getId(),
            userAggregate.getUsername(),
            userAggregate.getEmail(),
            userAggregate.getBio(),
            userAggregate.getProfileImage(),
            userAggregate.getCreatedAt(),
            userAggregate.getUpdatedAt(),
            userAggregate.isDeleted());
    if (userNode.deleted()) {
      neo4jRepository.deleteNode(userNode.deleteCypher());
      // delete all relations of the node
      neo4jRepository.deleteRelation(userNode.deleteRelationshipCypher());
    } else {
      neo4jRepository.createOrUpdateNode(userNode.createOrUpdateCypher());
    }
  }

  public void likePost(String postId) {
    logger()
        .info(
            "Running like cypher script with : \n userId: {}\npostId: {}",
            authService.getUserId(),
            postId);

    UserNode userNode = new UserNode(authService.getUserId());
    PostNode postNode = new PostNode(postId);

    PostNode completePostNode = neo4jRepository.getPost(postNode.findCypher());
    if (completePostNode == null || completePostNode.deleted()) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("Post not found");
    }
    UserNode PostOwnerNode = new UserNode(completePostNode.ownerId());
    throwErrorIfBlockRelationshipExists(userNode, PostOwnerNode, "like post of");

    LikeRelationship likeRelationship = new LikeRelationship(userNode, postNode);

    neo4jRepository.createRelation(likeRelationship.createCypher());

    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        LikeCommand.builder()
            .userId(authService.getUserId())
            .postId(postId)
            .liked(true)
            .createdAt(likeRelationship.createdAt())
            .build());
  }

  public void unlikePost(String postId) {
    logger()
        .info(
            "Running unlike cypher script with : \n userId: {}\npostId: {}",
            authService.getUserId(),
            postId);

    UserNode userNode = new UserNode(authService.getUserId());
    PostNode postNode = new PostNode(postId);

    PostNode completePostNode = neo4jRepository.getPost(postNode.findCypher());
    if (completePostNode == null || completePostNode.deleted()) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("Post not found");
    }
    UserNode PostOwnerNode = new UserNode(completePostNode.ownerId());
    throwErrorIfBlockRelationshipExists(userNode, PostOwnerNode, "like post of");

    LikeRelationship likeRelationship = new LikeRelationship(userNode, postNode);

    neo4jRepository.deleteRelation(likeRelationship.deleteCypher());

    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        LikeCommand.builder()
            .userId(authService.getUserId())
            .postId(postId)
            .liked(false)
            .createdAt(likeRelationship.createdAt())
            .build());
  }

  public void followUser(String userId) {
    logger()
        .info(
            "Running follow cypher script with : \n current userId: {}\ntarget userId: {}\n",
            authService.getUserId(),
            userId);

    // Cannot follow yourself
    if (userId.equals(authService.getUserId())) {
      throw RepoSocialErrorCode.BAD_REQUEST.createError("Cannot follow yourself");
    }

    UserNode followerNode = new UserNode(authService.getUserId());
    UserNode followedNode = new UserNode(userId);
    UserNode completeFollowedNode = neo4jRepository.getUser(followedNode.findCypher());

    if (completeFollowedNode == null || completeFollowedNode.deleted()) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }

    throwErrorIfBlockRelationshipExists(followerNode, followedNode, "follow");

    FollowRelationship followRelationship = new FollowRelationship(followerNode, followedNode);
    neo4jRepository.createRelation(followRelationship.createCypher());

    // Redis
    redisPublisher.publish(
        followCommandChannel,
        FollowCommand.builder()
            .userId(authService.getUserId())
            .followerId(userId)
            .following(true)
            .build());
  }

  public void unfollowUser(String userId) {
    logger()
        .info(
            "Running unfollow cypher script with : \n current userId: {}\ntarget userId: {}\n",
            authService.getUserId(),
            userId);

    // Cannot unfollow yourself
    if (userId.equals(authService.getUserId())) {
      throw RepoSocialErrorCode.BAD_REQUEST.createError("Cannot unfollow yourself");
    }

    UserNode followerNode = new UserNode(authService.getUserId());
    UserNode followedNode = new UserNode(userId);
    UserNode completeFollowedNode = neo4jRepository.getUser(followedNode.findCypher());

    if (completeFollowedNode == null || completeFollowedNode.deleted()) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }

    throwErrorIfBlockRelationshipExists(followerNode, followedNode, "unfollow");

    FollowRelationship followRelationship = new FollowRelationship(followerNode, followedNode);
    neo4jRepository.deleteRelation(followRelationship.deleteCypher());

    // Redis
    redisPublisher.publish(
        followCommandChannel,
        FollowCommand.builder()
            .userId(authService.getUserId())
            .followerId(userId)
            .following(false)
            .build());
  }

  public void blockUser(String userId) {
    logger()
        .info(
            "Running block cypher script with : \n current userId: {}\ntarget userId: {}\n",
            authService.getUserId(),
            userId);

    // Cannot block yourself
    if (userId.equals(authService.getUserId())) {
      throw RepoSocialErrorCode.BAD_REQUEST.createError("Cannot block yourself");
    }

    UserNode blockerNode = new UserNode(authService.getUserId());
    UserNode blockedNode = new UserNode(userId);
    UserNode completeBlockedNode = neo4jRepository.getUser(blockedNode.findCypher());

    if (completeBlockedNode == null || completeBlockedNode.deleted()) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }

    // Delete follow relation with the user (Instagram behavior)
    FollowRelationship blockerFollowRelationship = new FollowRelationship(blockerNode, blockedNode);
    neo4jRepository.deleteRelation(blockerFollowRelationship.deleteCypher());
    FollowRelationship blockedFollowRelationship = new FollowRelationship(blockedNode, blockerNode);
    neo4jRepository.deleteRelation(blockedFollowRelationship.deleteCypher());

    BlockRelationship blockRelationship = new BlockRelationship(blockerNode, blockedNode);
    neo4jRepository.createRelation(blockRelationship.createCypher());

    // Redis
    redisPublisher.publish(
        blockCommandChannel,
        BlockCommand.builder()
            .userId(authService.getUserId())
            .targetId(userId)
            .blocked(true)
            .build());
  }

  public void unblockUser(String userId) {
    logger()
        .info(
            "Running unblock cypher script with : \n current userId: {}\ntarget userId: {}\n",
            authService.getUserId(),
            userId);

    // Cannot unblock yourself
    if (userId.equals(authService.getUserId())) {
      throw RepoSocialErrorCode.BAD_REQUEST.createError("Cannot unblock yourself");
    }

    UserNode blockerNode = new UserNode(authService.getUserId());
    UserNode blockedNode = new UserNode(userId);
    UserNode completeBlockedNode = neo4jRepository.getUser(blockedNode.findCypher());

    if (completeBlockedNode == null || completeBlockedNode.deleted()) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }

    BlockRelationship blockRelationship = new BlockRelationship(blockerNode, blockedNode);
    neo4jRepository.deleteRelation(blockRelationship.deleteCypher());

    // Redis
    redisPublisher.publish(
        blockCommandChannel,
        BlockCommand.builder()
            .userId(authService.getUserId())
            .targetId(userId)
            .blocked(false)
            .build());
  }

  public List<UserEntity> getPostLikes(String postId) {
    logger().info("Running getPostLikes cypher script with : \n postId: {}\n", postId);

    PostNode postNode = new PostNode(postId);
    PostNode completePostNode = neo4jRepository.getPost(postNode.findCypher());
    if (completePostNode == null) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("Post not found");
    }
    UserNode postOwnerNode = new UserNode(completePostNode.ownerId());
    UserNode currentUserNode = new UserNode(authService.getUserId());
    throwErrorIfBlockRelationshipExists(currentUserNode, postOwnerNode, "get likes of the post of");

    logger().info(postNode.getLikesCypher());

    return neo4jRepository.getUsers(postNode.getLikesCypher()).stream()
        .map(userNodeToUserEntity::convertNotNull)
        .toList();
  }

  public List<PostEntity> getUserLikedPosts(String userId) {
    logger().info("Running getUserLikedPosts cypher script with : \n userId: {}\n", userId);

    UserNode userNode = new UserNode(userId);
    if (!neo4jRepository.checkNodeExists(userNode.findCypher())) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }
    UserNode currentUserNode = new UserNode(authService.getUserId());
    throwErrorIfBlockRelationshipExists(currentUserNode, userNode, "get liked posts of");

    return neo4jRepository.getPosts(userNode.getLikedPostsCypher()).stream()
        .map(postNodeToPostEntity::convertNotNull)
        .toList();
  }

  public List<UserEntity> getUserFollowers(String userId) {
    logger().info("Running getUserFollowers cypher script with : \n userId: {}\n", userId);

    UserNode userNode = new UserNode(userId);
    if (!neo4jRepository.checkNodeExists(userNode.findCypher())) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }
    UserNode currentUserNode = new UserNode(authService.getUserId());
    throwErrorIfBlockRelationshipExists(currentUserNode, userNode, "get followers of");

    return neo4jRepository.getUsers(userNode.getFollowersCypher()).stream()
        .map(userNodeToUserEntity::convertNotNull)
        .toList();
  }

  public List<UserEntity> getUserFollowings(String userId) {
    logger().info("Running getUserFollowings cypher script with : \n userId: {}\n", userId);

    UserNode userNode = new UserNode(userId);
    if (!neo4jRepository.checkNodeExists(userNode.findCypher())) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }
    UserNode currentUserNode = new UserNode(authService.getUserId());
    throwErrorIfBlockRelationshipExists(currentUserNode, userNode, "get users followed by");

    return neo4jRepository.getUsers(userNode.getFollowingsCypher()).stream()
        .map(userNodeToUserEntity::convertNotNull)
        .toList();
  }

  public List<UserEntity> getUserBlocked(String userId) {
    logger().info("Running getUserBlocked cypher script with : \n userId: {}\n", userId);

    UserNode userNode = new UserNode(userId);
    if (!neo4jRepository.checkNodeExists(userNode.findCypher())) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }
    UserNode currentUserNode = new UserNode(authService.getUserId());
    throwErrorIfBlockRelationshipExists(currentUserNode, userNode, "get blocked users of");

    return neo4jRepository.getUsers(userNode.getBlockedCypher()).stream()
        .map(userNodeToUserEntity::convertNotNull)
        .toList();
  }

  public List<UserEntity> getUserBlockedBy(String userId) {
    logger().info("Running getUserBlockedBy cypher script with : \n userId: {}\n", userId);

    UserNode userNode = new UserNode(userId);
    if (!neo4jRepository.checkNodeExists(userNode.findCypher())) {
      throw RepoSocialErrorCode.NOT_FOUND.createError("User not found");
    }
    UserNode currentUserNode = new UserNode(authService.getUserId());
    throwErrorIfBlockRelationshipExists(currentUserNode, userNode, "get blocked users of");

    return neo4jRepository.getUsers(userNode.getBlockedByCypher()).stream()
        .map(userNodeToUserEntity::convertNotNull)
        .toList();
  }
}
