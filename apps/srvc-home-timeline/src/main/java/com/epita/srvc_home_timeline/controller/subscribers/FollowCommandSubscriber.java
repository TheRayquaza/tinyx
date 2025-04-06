package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.FollowCommand;
import com.epita.srvc_home_timeline.service.HomeTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.function.Consumer;

@Startup
@ApplicationScoped
public class FollowCommandSubscriber implements Consumer<FollowCommand> {
  @Inject HomeTimelineService homeTimelineService;
  private final PubSubCommands.RedisSubscriber subscriber;

  public FollowCommandSubscriber(final RedisDataSource ds) {
    subscriber = ds.pubsub(FollowCommand.class).subscribe("follow-command", this);
  }

  @Override
  public void accept(final FollowCommand message) {
    vertx.executeBlocking(
        future -> {
          if (message.isFollowing()) {
            homeTimelineService.follow(message.getUserId(), message.getFollowerId());
          }
          else {
            homeTimelineService.unfollow(message.getUserId(), message.getFollowerId());
          }
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
