package com.epita.srvc_search.repository;

import com.epita.srvc_search.SrvcSearchErrorCode;
import com.epita.srvc_search.repository.model.SearchModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SearchRepository implements PanacheMongoRepository<SearchModel> {
  public Optional<SearchModel> findByUserId(String userId) {
    return find("userId", userId).firstResultOptional();
  }

  public void createUser(SearchModel user) {
    if (findByUserId(user.getUserId()).isPresent()) {
      throw SrvcSearchErrorCode.USER_ALREADY_EXISTS.createError(user.getUserId());
    }
    persist(user);
  }

  public void blockUser(SearchModel user, String targetId) {
    if (!user.getBlockedUserIds().contains(targetId)) {
      user.getBlockedUserIds().add(targetId);
      persist(user);
    } else {
      throw SrvcSearchErrorCode.ALREADY_BLOCKED.createError(targetId, user.getUserId());
    }
  }

  public void unblockUser(SearchModel user, String targetId) {
    if (user.getBlockedUserIds().contains(targetId)) {
      user.getBlockedUserIds().remove(targetId);
      persist(user);
    } else {
      throw SrvcSearchErrorCode.NOT_BLOCKED.createError(targetId, user.getUserId());
    }
  }
}
