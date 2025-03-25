package com.epita.exchange.redis.service;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class RedisPublisher {

    private final PubSubCommands<String> publisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public RedisPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(String.class);
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
