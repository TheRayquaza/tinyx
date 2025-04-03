package com.epita.srvc_search.controller;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.utils.Logger;
import com.epita.srvc_search.service.SearchService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;

@Startup
@ApplicationScoped
public class RedisSubscriber implements Logger {

    private final PubSubCommands<PostAggregate> subscriber;

    @ConfigProperty(name = "repo.post.aggregate.channel", defaultValue = "post-aggregate")
    @Inject
    String channel;

    @Inject
    SearchService searchService;

    @Inject
    public RedisSubscriber(RedisDataSource redisDataSource) {
        this.subscriber = redisDataSource.pubsub(PostAggregate.class);
    }

    @PostConstruct
    void initialize() {
        subscriber.subscribe(channel, this::handleIncomingMessage);
        logger().info("Subscribed to channel: " + channel);
    }

    private void handleIncomingMessage(PostAggregate message) {
        logger().info("Received message: " + message);
        if (message.isDeleted()) {
            searchService.deletePost(message);
        } else {
            searchService.indexPost(message);
        }
    }
}