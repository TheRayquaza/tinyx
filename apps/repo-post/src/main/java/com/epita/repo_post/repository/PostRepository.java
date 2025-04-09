package com.epita.repo_post.repository;

import com.epita.repo_post.repository.model.PostModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepository<PostModel> {
  public List<PostModel> findAllReplies(String postId) {
    return find("replyToPostId", postId).list();
  }

  public Optional<PostModel> findByIdStringOptional(String postId) {
    try {
      return find("_id", new ObjectId(postId)).firstResultOptional();
    } catch (IllegalArgumentException exception) {
      return Optional.empty();
    }
  }

  public void create(PostModel model) {
    persist(model);
  }
}
