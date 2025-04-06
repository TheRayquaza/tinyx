package com.epita.srvc_search.controller;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.utils.Logger;
import com.epita.srvc_search.service.SearchService;
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

  private final PubSubCommands<PostAggregate> subscriberPost;
  private final PubSubCommands<BlockCommand> subscriberBlock;

  @ConfigProperty(name = "repo.post.aggregate.channel")
  @Inject
  String postChannel;

  @ConfigProperty(name = "block.command.channel")
  @Inject
  String blockChannel;

  @Inject SearchService searchService;

  @Inject
  public RedisSubscriber(RedisDataSource redisDataSource) {
    this.subscriberPost = redisDataSource.pubsub(PostAggregate.class);
    this.subscriberBlock = redisDataSource.pubsub(BlockCommand.class);
  }

  @PostConstruct
  void initialize() {
    subscriberPost.subscribe(postChannel, this::handlePostIncomingMessage);
    logger().info("Subscribed to channel: " + postChannel);
    subscriberBlock.subscribe(blockChannel, this::handleBlockIncomingMessage);
    logger().info("Subscribed to channel: " + blockChannel);
  }

  private void handlePostIncomingMessage(PostAggregate message) {
    logger().info("Received post message: " + message);
    if (message.isDeleted()) {
      searchService.deletePost(message);
    } else {
      searchService.indexPost(message);
    }
  }

  private void handleBlockIncomingMessage(BlockCommand message) {
    logger().info("Received block message: " + message);
    searchService.handleBlockCommand(message);
  }
}
