package com.epita.repo_post.repository;

import com.epita.repo_post.repository.model.BlockedModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BlockedRepository implements PanacheMongoRepository<BlockedModel> {
  public List<BlockedModel> findIfBlocked(String OwnerId, String IsHeBlockedId) {
    return find("userId", OwnerId, "targetId", IsHeBlockedId).list();
  }

  public void create(BlockedModel model) {
    persist(model);
  }
}
