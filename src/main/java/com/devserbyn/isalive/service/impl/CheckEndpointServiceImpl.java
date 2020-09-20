package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.User;
import com.devserbyn.isalive.repository.CheckEndpointRepository;
import com.devserbyn.isalive.service.CheckEndpointService;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckEndpointServiceImpl implements CheckEndpointService {

    private final CheckEndpointRepository repository;

    @Override
    public CheckEndpoint save(CheckEndpoint checkEndpoint) {
        if (repository.findById(checkEndpoint.getId()).isPresent() &&
            repository.findById(checkEndpoint.getId()).orElseThrow(IllegalStateException::new).differentFrom(checkEndpoint)) {
            checkEndpoint.setDateModified(new Date());
        }
        return repository.save(checkEndpoint);
    }

    @Override
    public List<CheckEndpoint> findAll() {
        return repository.findAll().stream()
                .filter(x -> !x.isArchived())
                .sorted(Comparator.comparing(CheckEndpoint::getDateCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckEndpoint> findAllByUser(User user) {
        return this.findAll().stream()
                .filter(endpoint -> endpoint.getUser().getId() == user.getId())
                .sorted(Comparator.comparing(CheckEndpoint::getDateCreated)).collect(Collectors.toList());
    }

    @Override
    public CheckEndpoint findLastAddedByUser(User user) {
        return this.findAll().stream().max(Comparator.comparing(CheckEndpoint::getDateCreated)).orElseThrow(RuntimeException::new);
    }

    @Override
    public void archive(CheckEndpoint checkEndpoint) {
        checkEndpoint.setArchived(true);
        checkEndpoint.setUser(null);
        checkEndpoint.setDateModified(new Date());
        this.save(checkEndpoint);
    }

    @Override
    public void archive(List<CheckEndpoint> checkEndpointList) {
        checkEndpointList.forEach(this::archive);
    }
}
