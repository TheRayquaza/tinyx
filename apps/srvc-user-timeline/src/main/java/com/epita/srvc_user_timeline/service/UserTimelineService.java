package com.epita.srvc_user_timeline.service;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.command.FollowCommand;
import com.epita.exchange.redis.command.LikeCommand;
import com.epita.srvc_user_timeline.UserTimelineErrorCode;
import com.epita.srvc_user_timeline.converter.UserTimelinePostEntityToUserTimelinePostModel;
import com.epita.srvc_user_timeline.converter.UserTimelinePostEntityToUserTimelinePostResponse;
import com.epita.srvc_user_timeline.converter.UserTimelinePostModelToUserTimelinePostEntity;
import com.epita.srvc_user_timeline.repository.UserTimelineRepository;
import com.epita.srvc_user_timeline.repository.model.UserTimelinePostModel;
import com.epita.srvc_user_timeline.service.entity.UserTimelineEntity;
import com.epita.srvc_user_timeline.service.entity.UserTimelinePostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class UserTimelineService {
    @Inject
    UserTimelineRepository userTimelineRepository;
    @Inject
    UserTimelinePostModelToUserTimelinePostEntity
            userTimelinePostModelToUserTimelinePostEntity;
    @Inject
    UserTimelinePostEntityToUserTimelinePostModel
            userTimelinePostEntityToUserTimelinePostModel;
    @Inject
    Logger logger;

    private List<UserTimelinePostEntity> sortPosts(List<UserTimelinePostEntity> posts) {
        posts.sort(
                (post1, post2) -> {
                    if (post1.getLikedAt() != null && post2.getLikedAt() != null) {
                        return post1.getLikedAt().compareTo(post2.getLikedAt());
                    }
                    if (post1.getLikedAt() != null) {
                        return post1.getLikedAt().compareTo(post2.getCreatedAt());
                    }
                    if (post2.getLikedAt() != null) {
                        return post1.getCreatedAt().compareTo(post2.getLikedAt());
                    }
                    return post1.getCreatedAt().compareTo(post2.getCreatedAt());
                });
        return posts;
    }

    public UserTimelineEntity getUserTimeline(String userId) {
        // TODO use repository to fetch all posts useful for this user
        List<UserTimelinePostModel> posts = userTimelineRepository
                .find("userId", userId)
                .stream()
                .toList();

        if (posts == null || posts.isEmpty()) {
            throw UserTimelineErrorCode.USER_NOT_FOUND.createError(userId);
        }
        List<UserTimelinePostEntity> postsEntity = posts.stream()
                .map(userTimelinePostModelToUserTimelinePostEntity::convertNotNull)
                .toList();
        return new UserTimelineEntity()
                .withPosts(sortPosts(postsEntity));
    }

    public void handlePostAggregate(PostAggregate postAggregate) {
        if (postAggregate.isDeleted()) {
            userTimelineRepository.deleteById(new ObjectId(postAggregate.getId()));
            return;
        }


        UserTimelinePostEntity userTimelinePostEntity = new UserTimelinePostEntity()
                .withId(postAggregate.getId())
                .withUserId(postAggregate.getOwnerId())
                .withOwnerId(postAggregate.getOwnerId())
                .withText(postAggregate.getText())
                .withMedia(postAggregate.getMedia())
                .withRepostId(postAggregate.getRepostId())
                .withReplyToPostId(postAggregate.getReplyToPostId())
                .withIsReply(postAggregate.isReply())
                .withCreatedAt(postAggregate.getCreatedAt())
                .withUpdatedAt(postAggregate.getUpdatedAt())
                .withDeleted(postAggregate.isDeleted());

        userTimelineRepository.persist(
                userTimelinePostEntityToUserTimelinePostModel
                        .convertNotNull(userTimelinePostEntity));
    }

    public void handleUserAggregate(UserAggregate userAggregate) {
        if (!userAggregate.isDeleted())
            return;

        userTimelineRepository
                .delete("ownerId = ?1 or userId = ?1", userAggregate.getId());
    }

    public void handleLikeCommand(LikeCommand likeCommand) {

        UserTimelinePostModel likedPost = userTimelineRepository
                .findById(new ObjectId(likeCommand.getPostId()));
        if (likedPost == null) {
            return;
        }

        if (!likeCommand.isLiked()) {
            userTimelineRepository.delete(likedPost);
        } else {
            UserTimelinePostModel newTimelinePost = new UserTimelinePostModel()
                    .withId(likeCommand.getPostId())
                    .withUserId(likeCommand.getUserId())
                    .withOwnerId(likedPost.getOwnerId())
                    .withText(likedPost.getText())
                    .withMedia(likedPost.getMedia())
                    .withRepostId(likedPost.getRepostId())
                    .withCreatedAt(likedPost.getCreatedAt())
                    .withUpdatedAt(likedPost.getUpdatedAt())
                    .withLikedAt(LocalDateTime.now());

            userTimelineRepository.persist(newTimelinePost);

        }

    }

    public void handleBlockCommand(BlockCommand blockCommand) {
        if (!blockCommand.isBlocked())
            return;

        userTimelineRepository
                .delete("userId = ?1 and ownerId = ?2 or userId = ?2 and ownerId = ?1", blockCommand.getUserId(), blockCommand.getTargetId());
    }
}
