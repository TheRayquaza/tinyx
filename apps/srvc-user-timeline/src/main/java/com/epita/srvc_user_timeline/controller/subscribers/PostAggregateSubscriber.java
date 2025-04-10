package com.epita.srvc_user_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.aggregate.PostAggregate;
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
public class PostAggregateSubscriber implements Consumer<PostAggregate> {
  String channel = System.getenv().getOrDefault("POST_AGGREGATE_CHANNEL", "post_aggregate");

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject UserTimelineService userTimelineService;

  public PostAggregateSubscriber(
      final RedisDataSource ds, UserTimelineService userTimelineService) {
    subscriber = ds.pubsub(PostAggregate.class).subscribe(channel, this);
    this.userTimelineService = userTimelineService;
  }

  @Override
  public void accept(final PostAggregate command) {
    vertx.executeBlocking(
        future -> {
          this.userTimelineService.handlePostAggregate(command);
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
