package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.constant.STR_CONSTANT;
import com.devserbyn.isalive.model.enums.ApplicationProperty;
import com.devserbyn.isalive.service.ApplicationService;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Arrays.asList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private Map<ApplicationProperty, String> applicationProperties = new HashMap<>();

    @Override
    public void checkEnvVarsPresence() {
        final List<String> requiredVars = asList(STR_CONSTANT.BOT_USERNAME_ENV_VAR, STR_CONSTANT.BOT_TOKEN_ENV_VAR);
        final List<String> extraVars = Collections.singletonList(STR_CONSTANT.SERVER_URL_ENV_VAR);
        // Handling required env vars
        List<String> absentReqVars = requiredVars.stream().filter(reqVar -> System.getenv().get(reqVar) == null).collect(Collectors.toList());
        if (!absentReqVars.isEmpty()) {
            absentReqVars.forEach(x -> log.error(String.format("Required environment variable missing: %s", x)));
            throw new RuntimeException("System can't be initialized without missing required variables");
        }
        // Handling extra env vars
        extraVars.stream().filter(extraVar -> System.getenv().get(extraVar) == null)
                          .forEach(x -> log.warn(String.format("Extra environment variable missing: %s", x)));
    }

    @Override
    public void setApplicationProperty(ApplicationProperty property, String value) {
        this.applicationProperties.put(property, value);
    }

    @Override
    public String getApplicationProperty(ApplicationProperty property) {
        return this.applicationProperties.get(property);
    }

    @Override
    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log.error("Something went wrong while thread was sleeping", e);
        }
    }


}
