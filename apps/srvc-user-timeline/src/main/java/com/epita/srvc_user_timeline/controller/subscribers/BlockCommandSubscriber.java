package com.epita.srvc_user_timeline.controller.subscribers;


import com.epita.exchange.redis.command.BlockCommand;
import com.epita.srvc_user_timeline.service.UserTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

@Startup
@ApplicationScoped
public class BlockCommandSubscriber implements Consumer<BlockCommand> {

    private final PubSubCommands.RedisSubscriber subscriber;
    @Inject
    UserTimelineService userTimelineService;

    public BlockCommandSubscriber(final RedisDataSource ds, UserTimelineService userTimelineService) {
        subscriber = ds.pubsub(BlockCommand.class)
                .subscribe("block_command", this);
        this.userTimelineService = userTimelineService;
    }

    @Override
    public void accept(final BlockCommand command) {
        vertx.executeBlocking(future -> {
            this.userTimelineService.handleBlockCommand(command);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}







