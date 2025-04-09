package com.epita.srvc_search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.epita.srvc_search.service.entity.SearchEntity;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

@ApplicationScoped
@Startup
public class ElasticSearchService {
  String searchElasticSearch = Optional.ofNullable(System.getenv("SRVC_SEARCH_ELASTIC_INDEX"))
          .orElse("srvc_search");

  @Inject ElasticsearchClient elasticsearchClient;

  public List<SearchEntity> search(final String searchString) throws IOException {
    final var searchTerms =
            Arrays.stream(searchString.split("\\s+")).filter(term -> !term.isEmpty()).toList();

    final List<Query> shouldQueries = searchTerms.stream()
            .map(term ->
                    Query.of(q -> q.match(
                            MatchQuery.of(m -> m
                                    .field("text")
                                    .query(term)
                            )
                    ))
            ).toList();

    final var request = SearchRequest.of(req -> req
            .index(searchElasticSearch)
            .query(q -> q
                    .bool(b -> b
                            .should(shouldQueries)
                    )
            )
    );

    final var response = elasticsearchClient.search(request, SearchEntity.class);
    return response.hits().hits().stream().map(Hit::source).toList();
  }

  public void indexPost(SearchEntity searchEntity) {
    try {
      //       Index the document in Elasticsearch
      elasticsearchClient.update(
          u ->
              u.index(searchElasticSearch)
                  .id(searchEntity.getId())
                  .doc(searchEntity)
                  .docAsUpsert(true), // Create the document if it doesn't exist
          SearchEntity.class);

      System.out.println("Post indexed successfully: " + searchEntity.getId());
    } catch (IOException e) {
      System.err.println("Error indexing post: " + e.getMessage());
    }
  }

  public void deleteIndex(SearchEntity searchEntity) {
    try {
      // Delete the document from Elasticsearch
      elasticsearchClient.delete(d -> d.index(searchElasticSearch).id(searchEntity.getId()));
    } catch (IOException e) {
      System.err.println("Error deleting document from Elasticsearch: " + e.getMessage());
    }
  }
}
