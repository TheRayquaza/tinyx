package com.epita.srvc_user_timeline;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class RedisPublisher {

  final PubSubCommands<String> publisher;
  final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Inject
  public RedisPublisher(RedisDataSource rd) {
    this.publisher = rd.pubsub(String.class);
  }

  public <T> void publish(String channel, T message) {
    try {
      String jsonMessage = objectMapper.writeValueAsString(message);
      publisher.publish(channel, jsonMessage);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize message", e);
    }
  }

  private <T> T parseMessage(String message, Class<T> modelClass) {
    try {
      return objectMapper.readValue(message, modelClass);
    } catch (IOException e) {
      throw new RuntimeException("Failed to deserialize message", e);
    }
  }
}
