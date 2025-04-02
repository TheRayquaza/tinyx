package com.epita.srvc_home_timeline.controller;

import com.epita.exchange.redis.command.BlockCommand;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Consumer;
import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

@Startup
@ApplicationScoped
public class BlockCommandSubscriber implements Consumer<BlockCommand> {
    private final PubSubCommands.RedisSubscriber subscriber;

    public BlockCommandSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(BlockCommand.class)
                .subscribe("block-command", this);
    }

    @Override
    public void accept(final BlockCommand message) {
        vertx.executeBlocking(future -> {
            future.complete();
        }) ;
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
