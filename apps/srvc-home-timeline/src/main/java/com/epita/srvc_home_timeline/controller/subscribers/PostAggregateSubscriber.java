package com.epita.srvc_home_timeline.controller.subscribers;

import com.epita.exchange.redis.aggregate.PostAggregate;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Consumer;
import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

@Startup
@ApplicationScoped
public class PostAggregateSubscriber implements Consumer<PostAggregate> {
    private final PubSubCommands.RedisSubscriber subscriber;

    public PostAggregateSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(PostAggregate.class)
                .subscribe("post-aggregate", this);
    }

    @Override
    public void accept(final PostAggregate message) {
        vertx.executeBlocking(future -> {
            future.complete();
        }) ;
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
