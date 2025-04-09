package com.epita.repo_post.controller;

import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.utils.Logger;
import com.epita.repo_post.service.PostService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Startup
@ApplicationScoped
public class RedisSubscriber implements Logger {

  private final PubSubCommands<BlockCommand> subscriber;

  @ConfigProperty(name = "repo.social.block.command.channel", defaultValue = "block-command")
  @Inject
  String channel;

  @Inject PostService postService;

  @Inject
  public RedisSubscriber(RedisDataSource redisDataSource) {
    this.subscriber = redisDataSource.pubsub(BlockCommand.class);
  }

  @PostConstruct
  void initialize() {
    subscriber.subscribe(channel, this::handleIncomingMessage);
    logger().info("Subscribed to channel: " + channel);
  }

  private void handleIncomingMessage(BlockCommand message) {
    logger().info("Received message: " + message);
    postService.updateBlockedPeople(message);
  }
}
