package com.epita.srvc_search.service;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.srvc_search.SrvcSearchErrorCode;
import com.epita.srvc_search.controller.request.SearchRequest;
import com.epita.srvc_search.converter.PostAggregateToSearchEntity;
import com.epita.srvc_search.repository.SearchRepository;
import com.epita.srvc_search.repository.model.SearchModel;
import com.epita.srvc_search.service.entity.SearchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class SearchService {

  @Inject ElasticSearchService elasticSearchService;

  @Inject SearchRepository searchRepository;

  @Inject PostAggregateToSearchEntity postAggregateToSearchEntity;

  public List<SearchEntity> searchPosts(String userId, SearchRequest request) {
    if (request == null || request.getQuery() == null) {
      throw SrvcSearchErrorCode.INVALID_QUERY.createError("request / text");
    }
    try {
      List<SearchEntity> searchEntities = elasticSearchService.search(request.getQuery());

      Optional<SearchModel> user = searchRepository.findByUserId(userId);

      return user.map(
              searchModel ->
                  searchEntities.stream()
                      .filter(
                          entity -> !searchModel.getBlockedUserIds().contains(entity.getOwnerId()))
                      .toList())
          .orElse(searchEntities);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void indexPost(PostAggregate postAggregate) {
    elasticSearchService.indexPost(postAggregateToSearchEntity.convertNotNull(postAggregate));
  }

  public void deletePost(PostAggregate postAggregate) {
    elasticSearchService.deleteIndex(postAggregateToSearchEntity.convertNotNull(postAggregate));
  }

  public void handleBlockCommand(BlockCommand blockCommand) {
    Optional<SearchModel> user = searchRepository.findByUserId(blockCommand.getUserId());

    if (user.isPresent()) {
      if (blockCommand.isBlocked()) {
        // Block the user
        searchRepository.blockUser(user.get(), blockCommand.getTargetId());
      } else {
        // Unblock the user
        searchRepository.unblockUser(user.get(), blockCommand.getTargetId());
      }
    } else {
      SearchModel searchModel = new SearchModel();
      searchModel.setUserId(blockCommand.getUserId());
      searchModel.setBlockedUserIds(Set.of(blockCommand.getTargetId()));
      searchRepository.createUser(searchModel);
    }
  }
}
