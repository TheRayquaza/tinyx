package com.epita.srvc_user_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.BlockCommand;
import com.epita.srvc_user_timeline.service.UserTimelineService;
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
  @Inject UserTimelineService userTimelineService;

  public BlockCommandSubscriber(final RedisDataSource ds, UserTimelineService userTimelineService) {
    subscriber = ds.pubsub(BlockCommand.class).subscribe(channel, this);
    this.userTimelineService = userTimelineService;
  }

  @Override
  public void accept(final BlockCommand command) {
    vertx.executeBlocking(
        future -> {
          this.userTimelineService.handleBlockCommand(command);
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
