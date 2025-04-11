package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.srvc_home_timeline.service.HomeTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.function.Consumer;

@Startup
@ApplicationScoped
public class UserAggregateSubscriber implements Consumer<UserAggregate> {
  String channel = System.getenv().getOrDefault("USER_AGGREGATE_CHANNEL", "user_aggregate");

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public UserAggregateSubscriber(
      final RedisDataSource ds, HomeTimelineService homeTimelineService) {
    subscriber = ds.pubsub(UserAggregate.class).subscribe("user_aggregate", this);
    this.homeTimelineService = homeTimelineService;
  }

  @Override
  public void accept(final UserAggregate message) {
    vertx.executeBlocking(
        future -> {
          if (message.isDeleted()) {
            this.homeTimelineService.handleUserDeletion(message.getId());
          } else {
            this.homeTimelineService.handleUserCreation(message.getId());
          }
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
