package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.FollowCommand;
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
public class FollowCommandSubscriber implements Consumer<FollowCommand> {
  String channel = System.getenv().getOrDefault("FOLLOW_COMMAND_CHANNEL", "follow_command");

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public FollowCommandSubscriber(
      final RedisDataSource ds, HomeTimelineService homeTimelineService) {
    subscriber = ds.pubsub(FollowCommand.class).subscribe(channel, this);
    this.homeTimelineService = homeTimelineService;
  }

  @Override
  public void accept(final FollowCommand message) {
    vertx.executeBlocking(
        future -> {
          if (message.isFollowing()) {
            this.homeTimelineService.handleFollow(message.getUserId(), message.getFollowerId());
          } else {
            this.homeTimelineService.handleUnfollow(message.getUserId(), message.getFollowerId());
          }
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
