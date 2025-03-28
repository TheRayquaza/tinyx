# Redis

## What is Redis used for ?

Redis is used as a Message broker to propagate data into the whole architecture.
It uses the pub/sub pattern (https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern).

## Publisher

A publisher has been declared to send raw JSON data into a channel with:

```java
public myService {

    @Inject RedisPublisher redisPublisher

    @ConfigProperty(name = "xxx")
    @Inject
    String myChannel;

...

    public void myServiceFunction() {
        ...
        redisPublisher(myChannel, myRawSerializableData)
        ...
    }

}
```

## Redis Subscriber

Redis subcriber scrapes new incoming data from a channel and updates the service

```java
@Startup
@ApplicationScoped
public class RedisSubscriber implements Logger {

    private final ReactivePubSubCommands<UserAggregate> subscriber;
    @ConfigProperty(name = "repo.user.aggregate.channel") public final String channel;

    @Inject
    public RedisSubscriber(RedisDataSource redisDataSource) {
        this.subscriber = redisDataSource.pubsub(UserAggregate.class).reactive();
    }

    @PostConstruct
    void initialize() {
        subscriber.subscribe(channel)
            .subscribe().with(message -> handleIncomingMessage(message));
    }

    private void handleIncomingMessage(UserAggregate message) {
        logger().info("Received message: " + message);
    }
}
```

## Debug

In docker compose, there is a redisinsight container that gives you a ui with the channels of redis

localhost:5540
