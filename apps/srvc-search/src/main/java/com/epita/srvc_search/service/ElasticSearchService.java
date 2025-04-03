package com.epita.srvc_search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.epita.srvc_search.service.entity.SearchEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class ElasticSearchService {
    @ConfigProperty(name = "srvc.search.aggregate.channel", defaultValue = "post-aggregate")
    @Inject
    String searchAggregateChannel;

    @Inject
    ElasticsearchClient elasticsearchClient;

    public List<SearchEntity> search(final String searchString) throws IOException {
        final var searchTerms = Arrays.stream(searchString.split("\\s+"))
                .filter(term -> !term.isEmpty())
                .toList();

        final var request = SearchRequest.of(requestBuilder -> requestBuilder
                .index(searchAggregateChannel)
                .query(queryBuilder -> queryBuilder
                                .match(MatchQuery.of(matchBuilder -> matchBuilder
                                        .field("text")
                                        .query(searchTerms
                                                .stream()
                                                .reduce("", (acc, term) -> acc + " " + term)
                                        )
                                )))
        );
        final var response = elasticsearchClient.search(request, SearchEntity.class);
        return response.hits().hits().stream().map(Hit::source).toList();
    }

    public void indexPost(SearchEntity searchEntity) {
        try {
            SearchEntity search = new SearchEntity();
            search.setId(searchEntity.getId());
            search.setText(searchEntity.getText());
            search.setOwnerId(searchEntity.getOwnerId());
            search.setCreatedAt(searchEntity.getCreatedAt());

            // Index the document in Elasticsearch
            elasticsearchClient.update(u -> u
                            .index(searchAggregateChannel)
                            .id(searchEntity.getId())
                            .doc(search)
                            .docAsUpsert(true), // Create the document if it doesn't exist
                    SearchEntity.class
            );

        } catch (IOException e) {
            System.err.println("Error indexing post: " + e.getMessage());
        }
    }

    public void deleteIndex(SearchEntity searchEntity) {
        try {
            // Delete the document from Elasticsearch
            elasticsearchClient.delete(d -> d
                    .index(searchAggregateChannel)
                    .id(searchEntity.getId())
            );
        } catch (IOException e) {
            System.err.println("Error deleting document from Elasticsearch: " + e.getMessage());
        }
    }
}
