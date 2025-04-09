package com.epita.repo_post.repository;

import com.epita.repo_post.repository.model.BlockedModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BlockedRepository implements PanacheMongoRepository<BlockedModel> {
  public List<BlockedModel> findIfBlocked(String ownerId, String targetId) {
    return find("userId = ?1 and targetId = ?2 and blocked = ?3", ownerId, targetId, true).list();
  }

  public void create(BlockedModel model) {
    persist(model);
  }
}
