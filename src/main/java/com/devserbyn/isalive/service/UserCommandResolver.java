package com.devserbyn.isalive.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserCommandResolver {

    String resolveStart(Update update);

    String resolveAdd(Update update);

    String resolveList(Update update);

    String resolveRemove(Update update);

    String resolveRemoveme(Update update);

    String resolveHelp(Update update);
}
