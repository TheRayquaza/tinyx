package com.epita.srvc_user_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.LikeCommand;
import com.epita.srvc_user_timeline.service.UserTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.function.Consumer;

@Startup
@ApplicationScoped
public class LikeCommandSubscriber implements Consumer<LikeCommand> {

  @Inject
  @ConfigProperty(name = "repo.social.like.command.channel", defaultValue = "like_command")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject UserTimelineService userTimelineService;

  public LikeCommandSubscriber(final RedisDataSource ds, UserTimelineService userTimelineService) {
    subscriber = ds.pubsub(LikeCommand.class).subscribe("like_command", this);
    this.userTimelineService = userTimelineService;
  }

  @Override
  public void accept(final LikeCommand command) {
    vertx.executeBlocking(
        future -> {
          this.userTimelineService.handleLikeCommand(command);
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
