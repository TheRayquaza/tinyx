package com.epita.repo_post.repository;

import com.epita.repo_post.repository.model.PostModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepository<PostModel> {
  public void create(PostModel model) {
    persist(model);
  }
}
