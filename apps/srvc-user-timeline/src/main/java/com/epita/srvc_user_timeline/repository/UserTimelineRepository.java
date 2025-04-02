package com.epita.srvc_user_timeline.repository;

import com.epita.srvc_user_timeline.repository.model.UserTimelinePostModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserTimelineRepository implements PanacheMongoRepository<UserTimelinePostModel> {}
