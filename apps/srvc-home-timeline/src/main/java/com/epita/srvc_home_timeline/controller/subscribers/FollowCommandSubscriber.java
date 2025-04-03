package com.epita.srvc_home_timeline.controller.subscribers;

import com.epita.exchange.redis.command.FollowCommand;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Consumer;
import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

@Startup
@ApplicationScoped
public class FollowCommandSubscriber implements Consumer<FollowCommand> {
    private final PubSubCommands.RedisSubscriber subscriber;

    public FollowCommandSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(FollowCommand.class)
                .subscribe("follow-command", this);
    }

    @Override
    public void accept(final FollowCommand message) {
        vertx.executeBlocking(future -> {
            future.complete();
        }) ;
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
