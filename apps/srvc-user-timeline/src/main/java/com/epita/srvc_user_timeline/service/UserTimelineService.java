package com.epita.srvc_user_timeline.service;

import com.epita.srvc_user_timeline.repository.UserTimelineRepository;
import com.epita.srvc_user_timeline.service.entity.UserTimelineEntity;
import com.epita.srvc_user_timeline.service.entity.UserTimelinePostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UserTimelineService {
  @Inject UserTimelineRepository userTimelineRepository;

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
    return new UserTimelineEntity();
  }
}
