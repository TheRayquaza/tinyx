package com.epita.srvc_search.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "search", database = "SrvcSearch")
public class SearchModel {
  @BsonId private ObjectId id;
  private String userId;
  private Set<String> blockedUserIds;
}
