package com.devserbyn.isalive.service;

import com.devserbyn.isalive.model.enums.TextResourceKeys;

public interface TextResourceService {

    public String get(TextResourceKeys textResourceKey);

    public void cacheAllTextResources();

    public void clearCaches();
}
