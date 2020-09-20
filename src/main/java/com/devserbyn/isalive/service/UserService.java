package com.devserbyn.isalive.service;

import com.devserbyn.isalive.model.User;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface UserService {

    User register(Update update);

    Optional<User> findByChatID(long chatID);

    void remove(Update update);

    boolean checkIfUserRegistered(long chatId);
}
