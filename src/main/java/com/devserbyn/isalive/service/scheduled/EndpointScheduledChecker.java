package com.devserbyn.isalive.service.scheduled;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.IsAliveBot;
import com.devserbyn.isalive.model.enums.EndpointCheckStatus;
import com.devserbyn.isalive.service.ApplicationService;
import com.devserbyn.isalive.service.CheckEndpointService;
import com.devserbyn.isalive.service.EndpointProcessingService;

import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devserbyn.isalive.model.enums.ApplicationProperty.ENDPOINTS_CHECK_IN_PROGRESS;
import static com.devserbyn.isalive.model.enums.EndpointCheckStatus.OK;
import static com.devserbyn.isalive.model.enums.EndpointCheckStatus.RESOLVED;

@Service
@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:cron_schedule.properties")
public class EndpointScheduledChecker {

    private final EndpointProcessingService endpointProcessingService;
    private final CheckEndpointService checkEndpointService;
    private final ApplicationService applicationService;
    private final IsAliveBot bot;

    private LocalDateTime jobStartedTime;

    @Scheduled(cron = "${cron.endpoint.checkInterval}")
    public void checkAllEndpoints() {
        log.debug("Check All Endpoints job started");
        applicationService.setApplicationProperty(ENDPOINTS_CHECK_IN_PROGRESS, "true");
        jobStartedTime = LocalDateTime.now();

        List<CheckEndpoint> checkEndpoints = checkEndpointService.findAll();
        Map<CheckEndpoint, EndpointCheckStatus> checkEndpointStatuses = new HashMap<>();
        checkEndpoints.forEach(curEndpoint -> {
            EndpointCheckStatus endpointCheckStatus = endpointProcessingService.checkEndpoint(curEndpoint);
            checkEndpointStatuses.put(curEndpoint, endpointCheckStatus);
        });
        // Sending bad or resolved statuses to users
        checkEndpointStatuses.forEach((curEndpoint, curStatus) -> {
            if (checkEndpointWasModified(curEndpoint)) {
                return;
            }
            if (curStatus == OK && curEndpoint.getLastCheckStatus() != OK) {
                bot.sendResponse(curEndpoint.getUser().getChatId(),
                                 endpointProcessingService.getAnswerOnEndpointCheckStatus(RESOLVED, curEndpoint));
            }
            if (curStatus != OK && curEndpoint.getLastCheckStatus() == OK) {
                bot.sendResponse(curEndpoint.getUser().getChatId(),
                                 endpointProcessingService.getAnswerOnEndpointCheckStatus(curStatus, curEndpoint));
            }
        });
        checkEndpointStatuses.forEach((curEndpoint, curStatus) -> {
            if (checkEndpointWasModified(curEndpoint)) {
                return;
            }
            curEndpoint.setLastCheckStatus(curStatus);
            checkEndpointService.save(curEndpoint);
        });

        applicationService.setApplicationProperty(ENDPOINTS_CHECK_IN_PROGRESS, "false");
        log.debug("Check All Endpoints job finished");
    }

    private boolean checkEndpointWasModified(CheckEndpoint endpoint) {
        return checkEndpointService.findById(endpoint.getId()).orElseThrow(IllegalStateException::new)
                                   .getDateModified().isAfter(jobStartedTime);
    }
}
