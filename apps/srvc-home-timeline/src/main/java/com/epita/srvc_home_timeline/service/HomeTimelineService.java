package com.epita.srvc_home_timeline.service;

import com.epita.srvc_home_timeline.repository.HomeTimelineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HomeTimelineService {
    @Inject HomeTimelineRepository homeTimelineRepository;
}
