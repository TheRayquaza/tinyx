package com.epita.srvc_user_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.command.BlockCommand;
import com.epita.srvc_user_timeline.service.UserTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.function.Consumer;

@Startup
@ApplicationScoped
public class BlockCommandSubscriber implements Consumer<BlockCommand> {

  @Inject
  @ConfigProperty(name = "repo.social.block.command.channel", defaultValue = "block_command")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject UserTimelineService userTimelineService;

  public BlockCommandSubscriber(final RedisDataSource ds, UserTimelineService userTimelineService) {
    subscriber = ds.pubsub(BlockCommand.class).subscribe("block_command", this);
    this.userTimelineService = userTimelineService;
  }

  @PostConstruct
  void init() {
    Config config = ConfigProvider.getConfig();
    System.out.println("=== All Quarkus Config Properties ===");
    for (String propertyName : config.getPropertyNames()) {
      String value = config.getOptionalValue(propertyName, String.class).orElse("<null>");
      System.out.println(propertyName + " = " + value);
    }
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
