package com.epita.repo_social.service;

import static org.neo4j.driver.Values.parameters;

import com.epita.exchange.auth.service.AuthService;
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
        "MATCH (u:UserModel {id: $userId}), (p:PostModel {id: $postId})\n"
            + "MERGE (u)-[:LIKED]->(p);\n";
    if (!session.executeWrite(
        tx ->
            tx.run(cypher, parameters("userId", authService.getUserId(), "postId", postId))
                .consume()
                .counters()
                .containsUpdates())) throw RepoSocialErrorCode.POST_NOT_FOUND.createError(postId);
    // Redis
    redisPublisher.publish(
        likeCommandChannel,
        new LikeCommand(UUID.randomUUID(), authService.getUserId(), postId, true));
  }
}
