package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.LikeCommand;
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
public class LikeCommandSubscriber implements Consumer<LikeCommand> {
  @Inject HomeTimelineService homeTimelineService;
  private final PubSubCommands.RedisSubscriber subscriber;

  public LikeCommandSubscriber(final RedisDataSource ds) {
    subscriber = ds.pubsub(LikeCommand.class).subscribe("like-command", this);
  }

  @Override
  public void accept(final LikeCommand message) {
    vertx.executeBlocking(
        future -> {
          if (message.isLiked()) {
            homeTimelineService.handleLike(
                message.getUserId(), message.getUuid().toString(), message.getFollowerId());
          } else {
            homeTimelineService.handleUnlike(message.getUserId(), message.getUuid().toString());
          }
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
