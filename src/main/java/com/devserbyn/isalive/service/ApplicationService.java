package com.devserbyn.isalive.service;

import com.devserbyn.isalive.model.enums.ApplicationProperty;

public interface ApplicationService {

    void checkEnvVarsPresence();

    void setApplicationProperty(ApplicationProperty property, String value);

    String getApplicationProperty(ApplicationProperty property);

    void sleep(long ms);
}
