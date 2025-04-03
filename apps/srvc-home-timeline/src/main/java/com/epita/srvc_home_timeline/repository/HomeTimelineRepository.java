package com.epita.srvc_home_timeline.repository;

import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineModel> {
}
