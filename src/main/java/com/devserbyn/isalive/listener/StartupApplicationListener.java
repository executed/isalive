package com.devserbyn.isalive.listener;

import com.devserbyn.isalive.constant.PATH_CONSTANT;
import com.devserbyn.isalive.service.ApplicationService;
import com.devserbyn.isalive.service.TextResourceService;
import com.devserbyn.isalive.utility.ResourceUtil;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartupApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

    private final ResourceUtil resourceUtil;
    private final TextResourceService textResourceService;
    private final ApplicationService applicationService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        applicationService.checkEnvVarsPresence();
        // Print banner saying bot initialized
        System.out.println(resourceUtil.readResourceFileLines(PATH_CONSTANT.BANNER_BOT_INIT));
        // Caching the text resources
        textResourceService.cacheAllTextResources();
    }
}
