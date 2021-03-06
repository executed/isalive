package com.devserbyn.isalive.controller;

import com.devserbyn.isalive.service.TextResourceService;
import com.devserbyn.isalive.service.UserInputResolver;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;

import static com.devserbyn.isalive.model.enums.TextResourceKeys.MSG_TYPE_NOT_SUP;

@Component
@RequiredArgsConstructor
public class InputController implements ApiController {

    private final UserInputResolver userInputResolver;
    private final TextResourceService textResourceService;

    @Override
    public String handle(Update update) {
        Message message = update.getMessage();
        if (message.isCommand()) {
            return userInputResolver.resolveCommand(update);
        } else if (message.getText() != null) {
            return userInputResolver.resolvePlainText(update);
        } else {
            return textResourceService.get(MSG_TYPE_NOT_SUP);
        }
    }
}
