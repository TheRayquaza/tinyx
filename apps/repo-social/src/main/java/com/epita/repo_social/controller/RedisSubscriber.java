package com.epita.repo_social.controller;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.utils.Logger;
import com.epita.repo_social.service.SocialService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Startup
@ApplicationScoped
public class RedisSubscriber implements Logger {

  private final PubSubCommands<PostAggregate> postSubscriber;
  private final PubSubCommands<UserAggregate> userSubscriber;

  String postChannel = System.getenv().getOrDefault("POST_AGGREGATE_CHANNEL", "post_aggregate");

  String userChannel =System.getenv().getOrDefault("USER_AGGREGATE_CHANNEL", "user_aggregate");

  @Inject SocialService socialService;

  @Inject
  public RedisSubscriber(RedisDataSource redisDataSource) {
    this.postSubscriber = redisDataSource.pubsub(PostAggregate.class);
    this.userSubscriber = redisDataSource.pubsub(UserAggregate.class);
  }

  @PostConstruct
  void initialize() {
    postSubscriber.subscribe(postChannel, this::handlePostAggregate);
    userSubscriber.subscribe(userChannel, this::handleUserAggregate);
    logger().info("Subscribed to channels: {}, {}", postChannel, userChannel);
  }

  private void handlePostAggregate(PostAggregate message) {
    logger().info("Received PostAggregate: {}", message);
    socialService.createOrUpdatePostFromAggregate(message);
  }

  private void handleUserAggregate(UserAggregate message) {
    logger().info("Received UserAggregate: {}", message);
    socialService.createOrUpdateUserFromAggregate(message);
  }
}
