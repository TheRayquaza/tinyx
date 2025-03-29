package com.epita.repo_post.repository;

import com.epita.repo_post.repository.model.PostModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepository<PostModel> {

  public Optional<PostModel> getById(String id) {
    return find("id", id).firstResultOptional();
  }

  public List<PostModel> findAllReplies(String postId) {
    return find("replyToPostId", postId).stream().toList();
  }

  public void create(PostModel model) {
    persist(model);
  }
}
