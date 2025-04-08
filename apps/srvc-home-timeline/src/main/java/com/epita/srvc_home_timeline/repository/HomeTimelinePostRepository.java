package com.epita.srvc_home_timeline.repository;

import com.epita.srvc_home_timeline.repository.model.HomeTimelinePostModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class HomeTimelinePostRepository implements PanacheMongoRepository<HomeTimelinePostModel> {
  public Optional<HomeTimelinePostModel> findByPostId(String postId) {
    return this.find("postId", postId).firstResultOptional();
  }

  public void update(HomeTimelinePostModel postModel) {
    this.update(postModel.getPostId(), postModel);
  }

  public void create(HomeTimelinePostModel postModel) {
    this.persist(postModel);
  }
}
