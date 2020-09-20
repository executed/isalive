package com.devserbyn.isalive.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserPlainTextResolver {

    String resolvePlainText(Update update);
}
