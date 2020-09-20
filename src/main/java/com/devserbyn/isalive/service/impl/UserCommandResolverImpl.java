package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.model.CheckEndpoint;
import com.devserbyn.isalive.model.User;
import com.devserbyn.isalive.model.UserConfig;
import com.devserbyn.isalive.model.UserSetupStepsBO;
import com.devserbyn.isalive.model.UserSetupStepsBO.UserSetupSteps;
import com.devserbyn.isalive.model.enums.TextResourceKeys;
import com.devserbyn.isalive.repository.UserConfigRepository;
import com.devserbyn.isalive.service.CheckEndpointService;
import com.devserbyn.isalive.service.TextResourceService;
import com.devserbyn.isalive.service.UserCommandResolver;
import com.devserbyn.isalive.service.UserService;
import com.devserbyn.isalive.utility.TextUtility;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.devserbyn.isalive.constant.INTEGER_CONSTANT.REMOVEME_EXPIRE_SEC;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ASK_FOR_APP_URL;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.ILLEGAL_REMOVE_NUM;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.REMOVEME;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.REMOVE_SUCCESS;
import static com.devserbyn.isalive.model.enums.TextResourceKeys.START_2ND_TIME;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandResolverImpl implements UserCommandResolver {

    private final UserService userService;
    private final UserSetupStepsBO userSetupStepsBO;
    private final TextResourceService textResourceService;
    private final UserConfigRepository userConfigRepository;
    private final CheckEndpointService checkEndpointService;

    @Override
    public String resolveStart(Update update) {
        if (userService.checkIfUserRegistered(update.getMessage().getChatId())) {
            return textResourceService.get(START_2ND_TIME);
        }
        User newUser = userService.register(update);

        UserConfig userConfig = new UserConfig();
        userConfig.setUser(newUser);
        userConfig.setSetupFinished(true);
        userConfigRepository.save(userConfig);

        List<UserSetupSteps> userSetupStepsList = userSetupStepsBO.getUserSetupStepsList();
        boolean isPresent = false;
        for (UserSetupSteps setupSteps : userSetupStepsList) {
            if (setupSteps.getChatId() == (update.getMessage().getChatId())) {
                isPresent = true;
                setupSteps.setWaitingForAppURL(true);
            }
        }
        if (!isPresent) {
            UserSetupSteps userSetupSteps = new UserSetupSteps();
            userSetupSteps.setChatId(update.getMessage().getChatId());
            userSetupSteps.setWaitingForAppURL(true);
            userSetupStepsList.add(userSetupSteps);
        }
        return textResourceService.get(ASK_FOR_APP_URL);
    }

    @Override
    public String resolveAdd(Update update) {
        if (!userService.checkIfUserRegistered(update.getMessage().getChatId())) {
            return textResourceService.get(TextResourceKeys.BOT_NOT_CONF_YET);
        }
        UserSetupSteps setupSteps = userSetupStepsBO.getOrCreateUserSetupStepsByChatId(update.getMessage().getChatId());
        setupSteps.setWaitingForAppURL(true);

        return textResourceService.get(ASK_FOR_APP_URL);
    }

    @Override
    public String resolveList(Update update) {
        if (!userService.checkIfUserRegistered(update.getMessage().getChatId())) {
            return textResourceService.get(TextResourceKeys.BOT_NOT_CONF_YET);
        }
        StringBuilder sb = new StringBuilder();
        List<CheckEndpoint> userEndpoints = checkEndpointService.findAllByUser(userService.findByChatID(update.getMessage().getChatId())
                                                                .orElseThrow(IllegalArgumentException::new));
        int rowCounter = 0;
        for (CheckEndpoint userEndpoint : userEndpoints) {
            sb.append(++rowCounter).append(". ").append(userEndpoint.getEndpointURL()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String resolveRemove(Update update) {
        String input = update.getMessage().getText();
        if (!userService.checkIfUserRegistered(update.getMessage().getChatId())) {
            return textResourceService.get(TextResourceKeys.BOT_NOT_CONF_YET);
        }
        UserSetupSteps userSetupSteps = userSetupStepsBO.getOrCreateUserSetupStepsByChatId(update.getMessage().getChatId());
        if (userSetupSteps.isWaitingForRemoveID()) {
            List<CheckEndpoint> allByUser = checkEndpointService.findAllByUser(userService.findByChatID(update.getMessage().getChatId())
                                                                .orElseThrow(IllegalStateException::new));
            if (!TextUtility.isNumber(input) || Integer.parseInt(input) > (allByUser.size())) {
                return textResourceService.get(ILLEGAL_REMOVE_NUM);
            }
            CheckEndpoint removeCandidate = allByUser.get(Integer.parseInt(input) - 1);
            checkEndpointService.archive(removeCandidate);
            userSetupStepsBO.removeByChatId(update.getMessage().getChatId());

            return String.format(textResourceService.get(REMOVE_SUCCESS), removeCandidate.getEndpointURL());
        }
        UserSetupSteps setupSteps = userSetupStepsBO.getOrCreateUserSetupStepsByChatId(update.getMessage().getChatId());
        setupSteps.setWaitingForRemoveID(true);

        return String.format(textResourceService.get(TextResourceKeys.ASK_FOR_REMOVE_ID), this.resolveList(update));
    }

    @Override
    public String resolveRemoveme(Update update) {
        Long chatId = update.getMessage().getChatId();
        UserSetupSteps userSetupSteps = null;
        for (UserSetupSteps setupSteps : userSetupStepsBO.getUserSetupStepsList()) {
            if (setupSteps.getChatId() == update.getMessage().getChatId()) {
                userSetupSteps = setupSteps;
            }
        }
        if (userSetupSteps == null) {
            userSetupSteps = new UserSetupSteps(chatId);
            userSetupStepsBO.getUserSetupStepsList().add(userSetupSteps);
        }
        boolean removeMeExpired = ChronoUnit.SECONDS
                .between(userSetupSteps.getRemovemeSentTime(), LocalDateTime.now()) > REMOVEME_EXPIRE_SEC;
        if (removeMeExpired) {
            // Setting removeme command sent time
            userSetupSteps.setRemovemeSentTime(LocalDateTime.now());
            return textResourceService.get(TextResourceKeys.REMOVEME_CONFIRM);
        }
        checkEndpointService.archive(checkEndpointService.findAllByUser(userService.findByChatID(chatId).orElseThrow(RuntimeException::new)));
        userService.remove(update);
        userSetupStepsBO.removeByChatId(chatId);

        return textResourceService.get(REMOVEME);
    }

    @Override
    public String resolveHelp(Update update) {
        return textResourceService.get(TextResourceKeys.HELP_CMD);
    }
}
