package com.epita.exchange.redis.subscriber;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.ReactivePubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;

@ApplicationScoped
public class RedisSubscriber implements Logger {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;

    private final ReactivePubSubCommands<String> subscriber;
    private final Driver neo4jDriver;
    private final ObjectMapper objectMapper;

    @ConfigProperty(name = "repo.user.aggregate.channel")
    String userAggregateChannel;

    @ConfigProperty(name = "repo.post.aggregate.channel")
    String postAggregateChannel;

    @Inject
    public RedisSubscriber(RedisDataSource redisDataSource, Driver neo4jDriver) {
        this.subscriber = redisDataSource.pubsub(String.class).reactive();
        this.neo4jDriver = neo4jDriver;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    void initialize() {
        createIndexes();
        subscriber.subscribe(userAggregateChannel)
                .subscribe().with(message -> handleMessageWithRetry(message, UserAggregate.class, this::handleUserAggregate));
        
        subscriber.subscribe(postAggregateChannel)
                .subscribe().with(message -> handleMessageWithRetry(message, PostAggregate.class, this::handlePostAggregate));
    }

    private void createIndexes() {
        try (var session = neo4jDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("CREATE CONSTRAINT user_id IF NOT EXISTS FOR (u:UserModel) REQUIRE u.id IS UNIQUE");
                tx.run("CREATE CONSTRAINT post_id IF NOT EXISTS FOR (p:PostModel) REQUIRE p.id IS UNIQUE");
                return null;
            });
        }
    }

    private <T> void handleMessageWithRetry(String message, Class<T> aggregateType, java.util.function.Consumer<T> handler) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                T aggregate = objectMapper.readValue(message, aggregateType);
                handler.accept(aggregate);
                return;
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                logger().error("Invalid message format: " + message, e);
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount == MAX_RETRIES) {
                    logger().error("Failed to process message after " + MAX_RETRIES + " attempts: " + message, e);
                    return;
                }
                try {
                    Thread.sleep(INITIAL_RETRY_DELAY_MS * (long) Math.pow(2, retryCount - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void handleUserAggregate(UserAggregate user) {
        logger().info("Received user aggregate: " + user);
        try (var session = neo4jDriver.session()) {
            if (user.isDeleted()) {
                session.executeWrite(tx -> {
                    // Delete user's posts first
                    tx.run("MATCH (u:UserModel {id: $userId})<-[:OWNED_BY]-(p:PostModel) DETACH DELETE p",
                        Values.parameters("userId", user.getId())).consume();
                    // Then delete the user
                    return tx.run("MATCH (u:UserModel {id: $userId}) DETACH DELETE u",
                        Values.parameters("userId", user.getId())).consume();
                });
            } else {
                session.executeWrite(tx ->
                    tx.run("MERGE (u:UserModel {id: $userId}) " +
                          "ON CREATE SET u.username = $username, u.email = $email, " +
                          "u.bio = $bio, u.profileImage = $profileImage, " +
                          "u.createdAt = $createdAt, u.updatedAt = $updatedAt " +
                          "ON MATCH SET u.username = $username, u.email = $email, " +
                          "u.bio = $bio, u.profileImage = $profileImage, " +
                          "u.updatedAt = $updatedAt",
                        Values.parameters(
                            "userId", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "bio", user.getBio(),
                            "profileImage", user.getProfileImage(),
                            "createdAt", user.getCreatedAt(),
                            "updatedAt", user.getUpdatedAt()
                        )
                    ).consume()
                );
            }
        }
    }

    private void handlePostAggregate(PostAggregate post) {
        logger().info("Received post aggregate: " + post);
        try (var session = neo4jDriver.session()) {
            if (post.isDeleted()) {
                session.executeWrite(tx ->
                    tx.run("MATCH (p:PostModel {id: $postId}) DETACH DELETE p",
                        Values.parameters("postId", post.getId())).consume()
                );
            } else {
                session.executeWrite(tx ->
                    tx.run("MERGE (p:PostModel {id: $postId}) " +
                          "ON CREATE SET p.ownerId = $ownerId, p.text = $text, " +
                          "p.media = $media, p.repostId = $repostId, " +
                          "p.replyToPostId = $replyToPostId, p.isReply = $isReply, " +
                          "p.createdAt = $createdAt, p.updatedAt = $updatedAt " +
                          "ON MATCH SET p.text = $text, p.media = $media, " +
                          "p.updatedAt = $updatedAt " +
                          "WITH p " +
                          "MATCH (u:UserModel {id: $ownerId}) " +
                          "MERGE (p)-[:OWNED_BY]->(u)",
                        Values.parameters(
                            "postId", post.getId(),
                            "ownerId", post.getOwnerId(),
                            "text", post.getText(),
                            "media", post.getMedia(),
                            "repostId", post.getRepostId(),
                            "replyToPostId", post.getReplyToPostId(),
                            "isReply", post.isReply(),
                            "createdAt", post.getCreatedAt(),
                            "updatedAt", post.getUpdatedAt()
                        )
                    ).consume()
                );
            }
        }
    }
}
