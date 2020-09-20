package com.devserbyn.isalive.service;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.User;

import java.util.List;

public interface CheckEndpointService {

    CheckEndpoint save(CheckEndpoint checkEndpoint);

    List<CheckEndpoint> findAll();

    List<CheckEndpoint> findAllByUser(User user);

    CheckEndpoint findLastAddedByUser(User user);

    void archive(CheckEndpoint checkEndpoint);

    void archive(List<CheckEndpoint> checkEndpointList);
}
