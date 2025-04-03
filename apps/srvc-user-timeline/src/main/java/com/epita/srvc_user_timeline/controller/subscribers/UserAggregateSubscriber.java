package com.epita.srvc_user_timeline.controller.subscribers;

import com.epita.exchange.redis.aggregate.UserAggregate;
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
public class UserAggregateSubscriber implements Consumer<UserAggregate> {

    private final PubSubCommands.RedisSubscriber subscriber;
    @Inject
    UserTimelineService userTimelineService;

    public UserAggregateSubscriber(final RedisDataSource ds, UserTimelineService userTimelineService) {
        subscriber = ds.pubsub(UserAggregate.class)
                .subscribe("user_aggregate", this);
        this.userTimelineService = userTimelineService;
    }

    @Override
    public void accept(final UserAggregate command) {
        vertx.executeBlocking(future -> {
            this.userTimelineService.handleUserAggregate(command);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}







