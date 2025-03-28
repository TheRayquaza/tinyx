package com.epita.srvc_search.service;

import com.epita.srvc_search.SrvcSearchErrorCode;
import com.epita.srvc_search.service.entity.SearchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class SearchService {

    @Inject
    ElasticSearchService elasticSearchService;

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

    public void indexPost(SearchEntity request) {
        return;
    }

    public void updatePost(SearchEntity request) {
        return;
    }

    public void deletePost(SearchEntity request) {
        return;
    }
}
