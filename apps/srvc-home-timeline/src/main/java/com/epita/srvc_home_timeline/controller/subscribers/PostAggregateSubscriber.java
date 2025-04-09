package com.epita.srvc_home_timeline.controller.subscribers;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.epita.exchange.redis.aggregate.PostAggregate;
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
public class PostAggregateSubscriber implements Consumer<PostAggregate> {
  @Inject
  @ConfigProperty(name = "repo.post.aggregate.channel", defaultValue = "post_aggregate")
  String channel;

  private final PubSubCommands.RedisSubscriber subscriber;
  @Inject HomeTimelineService homeTimelineService;

  public PostAggregateSubscriber(final RedisDataSource ds) {
    subscriber = ds.pubsub(PostAggregate.class).subscribe("post_aggregate", this);
  }

  @Override
  public void accept(final PostAggregate message) {
    vertx.executeBlocking(
        future -> {
          homeTimelineService.handlePostAggregate(message);
          future.complete();
        });
  }

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
