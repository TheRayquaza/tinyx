package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.BlockCommand;
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
public class BlockCommandSubscriber implements Consumer<BlockCommand> {
  String channel = System.getenv().getOrDefault("BLOCK_COMMAND_CHANNEL", "block_command");

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public BlockCommandSubscriber(final RedisDataSource ds, HomeTimelineService homeTimelineService) {
    subscriber = ds.pubsub(BlockCommand.class).subscribe(channel, this);
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
