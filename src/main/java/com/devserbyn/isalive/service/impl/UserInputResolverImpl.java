package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.model.enums.BotCommands;
import com.devserbyn.isalive.repository.UserConfigRepository;
import com.devserbyn.isalive.service.TextResourceService;
import com.devserbyn.isalive.service.UserCommandResolver;
import com.devserbyn.isalive.service.UserInputResolver;
import com.devserbyn.isalive.service.UserPlainTextResolver;
import com.devserbyn.isalive.service.UserRepositoryService;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

import static com.devserbyn.isalive.model.enums.TextResourceKeys.CMD_NOT_AVAILABLE;

@Service
@RequiredArgsConstructor
public class UserInputResolverImpl implements UserInputResolver {

    private final UserCommandResolver userCommandResolver;
    private final UserPlainTextResolver userPlainTextResolver;
    private final TextResourceService textResourceService;

    @Override
    public String resolveCommand(Update update) {
        BotCommands command = Arrays.stream(BotCommands.values())
                .filter(curCmd -> curCmd.getCommand().equals(update.getMessage().getText()))
                .findFirst().orElse(BotCommands.NOT_EXISTING);
        switch (command) {
            case START: return userCommandResolver.resolveStart(update);
            case ADD: return userCommandResolver.resolveAdd(update);
            case LIST: return userCommandResolver.resolveList(update);
            case REMOVE: return userCommandResolver.resolveRemove(update);
            case REMOVEME: return userCommandResolver.resolveRemoveme(update);
            case HELP: return userCommandResolver.resolveHelp(update);

            default: return textResourceService.get(CMD_NOT_AVAILABLE);
        }
    }

    @Override
    public String resolvePlainText(Update update) {
        return userPlainTextResolver.resolvePlainText(update);
    }
}
