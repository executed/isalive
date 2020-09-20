package com.devserbyn.isalive.service;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.UserSetupStepsBO.UserSetupSteps;
import com.devserbyn.isalive.model.enums.EndpointCheckStatus;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface EndpointProcessingService {

    String resolveEndpointSetup(Update update, UserSetupSteps userSetupSteps);

    EndpointCheckStatus checkEndpoint(CheckEndpoint endpoint);

    String getAnswerOnEndpointCheckStatus(EndpointCheckStatus status, CheckEndpoint checkEndpoint);
}
