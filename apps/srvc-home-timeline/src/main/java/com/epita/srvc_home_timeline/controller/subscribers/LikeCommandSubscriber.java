package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.LikeCommand;
import com.epita.srvc_home_timeline.service.HomeTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.function.Consumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Startup
@ApplicationScoped
public class LikeCommandSubscriber implements Consumer<LikeCommand> {
  @Inject
  @ConfigProperty(name = "repo.social.command.channel", defaultValue = "like_command")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public LikeCommandSubscriber(final RedisDataSource ds, HomeTimelineService homeTimelineService) {
    subscriber = ds.pubsub(LikeCommand.class).subscribe("like_command", this);
    this.homeTimelineService = homeTimelineService;
  }

  @Override
  public void accept(final LikeCommand message) {
    vertx.executeBlocking(
        future -> {
          if (message.isLiked()) {
            this.homeTimelineService.handleLike(message.getUserId(), message.getPostId());
          } else {
            this.homeTimelineService.handleUnlike(message.getUserId(), message.getPostId());
          }
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
