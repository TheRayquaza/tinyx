package com.epita.srvc_search.service;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.srvc_search.SrvcSearchErrorCode;
import com.epita.srvc_search.converter.PostAggregateToSearchEntity;
import com.epita.srvc_search.service.entity.SearchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class SearchService {

    @Inject
    ElasticSearchService elasticSearchService;

    @Inject
    PostAggregateToSearchEntity postAggregateToSearchEntity;

    public List<SearchEntity> searchPosts(SearchEntity request) {
        if (request == null || request.getText() == null) {
            throw SrvcSearchErrorCode.INVALID_QUERY.createError("request / text");
        }
        try {
            return elasticSearchService.search(request.getText());
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
}
