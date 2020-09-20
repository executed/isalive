package com.devserbyn.isalive.service;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotErrorHandler {

    void handleSendingMessageException(long chatId, String message, TelegramApiException e);
}
