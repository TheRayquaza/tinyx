package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.BlockCommand;
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
public class BlockCommandSubscriber implements Consumer<BlockCommand> {
  @Inject
  @ConfigProperty(name = "repo.social.command.channel", defaultValue = "block_command")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public BlockCommandSubscriber(final RedisDataSource ds, HomeTimelineService homeTimelineService) {
    subscriber = ds.pubsub(BlockCommand.class).subscribe("block_command", this);
    this.homeTimelineService = homeTimelineService;
  }

  @Override
  public void accept(final BlockCommand message) {
    vertx.executeBlocking(
        future -> {
          if (message.isBlocked()) {
            this.homeTimelineService.handleBlock(message.getUserId(), message.getTargetId());
          } else {
            this.homeTimelineService.handleUnblock(message.getUserId(), message.getTargetId());
          }
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
