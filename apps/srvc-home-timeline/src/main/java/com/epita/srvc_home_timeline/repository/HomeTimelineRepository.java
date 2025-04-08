package com.epita.srvc_home_timeline.repository;

import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineModel> {
  public Optional<HomeTimelineModel> findByUserId(String userId) {
    return this.find("userId", userId).firstResultOptional();
  }

  public void updateModel(HomeTimelineModel homeTimelineModel) {
    this.update(homeTimelineModel.getId(), homeTimelineModel);
  }

  public void create(HomeTimelineModel homeTimelineModel) {
    this.persist(homeTimelineModel);
  }

  public List<HomeTimelineModel> getHomeTimelineContainingPostId(String postId) {
    return this.find("entries.PostId", postId).list();
  }

  public List<HomeTimelineModel> getHomeTimelineContainingUserId(String userId) {
    return this.find("followers", userId).list();
  }
}
