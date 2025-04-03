package com.epita.srvc_user_timeline.controller.subscribers;


import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.FollowCommand;
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
public class FollowCommandSubscriber implements Consumer<FollowCommand> {

    private final PubSubCommands.RedisSubscriber subscriber;
    @Inject
    UserTimelineService userTimelineService;

    public FollowCommandSubscriber(final RedisDataSource ds, UserTimelineService userTimelineService) {
        subscriber = ds.pubsub(FollowCommand.class)
                .subscribe("follow_command", this);
        this.userTimelineService = userTimelineService;
    }

    @Override
    public void accept(final FollowCommand command) {
        vertx.executeBlocking(future -> {
            this.userTimelineService.handleFollowCommand(command);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}







