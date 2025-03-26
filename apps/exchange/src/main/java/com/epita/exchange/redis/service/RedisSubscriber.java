package com.epita.exchange.redis.service;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.function.Consumer;

public abstract class RedisSubscriber<T> implements Consumer<String> {

  private final PubSubCommands.RedisSubscriber subscriber;
  private final ObjectMapper objectMapper;
  private final Class<T> type;

  public RedisSubscriber(RedisDataSource ds, String channel, Class<T> type) {
    this.subscriber = ds.pubsub(String.class).subscribe(channel, this);
    this.objectMapper = new ObjectMapper();
    this.type = type;
  }

  @Override
  public void accept(String message) {
    vertx.executeBlocking(
        future -> {
          try {
            handleMessage(objectMapper.readValue(message, type));
          } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize message", e);
          }
          future.complete();
        });
  }

  protected abstract void handleMessage(T message);

  @PreDestroy
  public void terminate() {
    subscriber.unsubscribe();
  }
}
