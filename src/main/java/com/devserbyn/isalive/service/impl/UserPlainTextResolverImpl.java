package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.model.UserSetupStepsBO;
import com.devserbyn.isalive.model.UserSetupStepsBO.UserSetupSteps;
import com.devserbyn.isalive.model.enums.TextResourceKeys;
import com.devserbyn.isalive.repository.UserConfigRepository;
import com.devserbyn.isalive.service.EndpointProcessingService;
import com.devserbyn.isalive.service.TextResourceService;
import com.devserbyn.isalive.service.UserCommandResolver;
import com.devserbyn.isalive.service.UserPlainTextResolver;
import com.devserbyn.isalive.service.UserService;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPlainTextResolverImpl implements UserPlainTextResolver {

    private final UserService userService;
    private final EndpointProcessingService endpointProcessingService;
    private final UserCommandResolver userCommandResolver;
    private final UserSetupStepsBO userSetupStepsBO;
    private final TextResourceService textResourceService;

    @Override
    public String resolvePlainText(Update update) {
        Long chatId = update.getMessage().getChatId();
        boolean userRegistered = userService.checkIfUserRegistered(chatId);
        Optional<UserSetupSteps> userSetupStepsOpt = userSetupStepsBO.getUserSetupStepsByChatId(chatId);

        if (!userSetupStepsOpt.isPresent() && !userRegistered) {
            return textResourceService.get(TextResourceKeys.BOT_NOT_CONF_YET);
        } else if (userRegistered && !userSetupStepsOpt.isPresent()) {
             return resolveText(update);
        } else if (userSetupStepsOpt.get().isWaitingForAppURL() || userSetupStepsOpt.get().isWaitingForSupportsIsAlive()) {
            return endpointProcessingService.resolveEndpointSetup(update, userSetupStepsOpt.get());
        } else if (userSetupStepsOpt.get().isWaitingForRemoveID()) {
            return userCommandResolver.resolveRemove(update);
        } else {
            throw new IllegalStateException("Uncovered situation when handling users plain text msg");
        }
    }



    private String resolveText(Update update) {
        return textResourceService.get(TextResourceKeys.MSG_TYPE_NOT_SUP);
    }
}
