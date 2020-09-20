package com.devserbyn.isalive.service.scheduled;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.IsAliveBot;
import com.devserbyn.isalive.model.enums.EndpointCheckStatus;
import com.devserbyn.isalive.service.CheckEndpointService;
import com.devserbyn.isalive.service.EndpointProcessingService;

import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devserbyn.isalive.model.enums.EndpointCheckStatus.OK;
import static com.devserbyn.isalive.model.enums.EndpointCheckStatus.RESOLVED;

@Service
@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:cron_schedule.properties")
public class EndpointScheduledChecker {

    private final EndpointProcessingService endpointProcessingService;
    private final CheckEndpointService checkEndpointService;
    private final IsAliveBot bot;

    @Scheduled(cron = "${cron.endpoint.checkInterval}")
    public void checkAllEndpoints() {
        log.trace("Check All Endpoints job started");

        List<CheckEndpoint> checkEndpoints = checkEndpointService.findAll();
        Map<CheckEndpoint, EndpointCheckStatus> checkEndpointStatuses = new HashMap<>();
        checkEndpoints.forEach(curEndpoint -> {
            EndpointCheckStatus endpointCheckStatus = endpointProcessingService.checkEndpoint(curEndpoint);
            checkEndpointStatuses.put(curEndpoint, endpointCheckStatus);
        });
        // Sending bad statuses to users
        checkEndpointStatuses.forEach((curEndpoint, curStatus) -> {
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
            curEndpoint.setLastCheckStatus(curStatus);
            checkEndpointService.save(curEndpoint);
        });

        log.trace("Check All Endpoints job finished");
    }

}
