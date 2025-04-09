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
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Startup
@ApplicationScoped
public class FollowCommandSubscriber implements Consumer<FollowCommand> {
  @Inject
  @ConfigProperty(name = "repo.social.command.channel", defaultValue = "follow_command")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public FollowCommandSubscriber(
      final RedisDataSource ds, HomeTimelineService homeTimelineService) {
    subscriber = ds.pubsub(FollowCommand.class).subscribe("follow_command", this);
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
