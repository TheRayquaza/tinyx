package com.epita.srvc_user_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.aggregate.UserAggregate;
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
public class UserAggregateSubscriber implements Consumer<UserAggregate> {

  @Inject
  @ConfigProperty(name = "repo.user.aggregate.channel", defaultValue = "user_aggregate")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject UserTimelineService userTimelineService;

  public UserAggregateSubscriber(
      final RedisDataSource ds, UserTimelineService userTimelineService) {
    subscriber = ds.pubsub(UserAggregate.class).subscribe("user_aggregate", this);
    this.userTimelineService = userTimelineService;
  }

  @Override
  public void accept(final UserAggregate command) {
    vertx.executeBlocking(
        future -> {
          this.userTimelineService.handleUserAggregate(command);
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
