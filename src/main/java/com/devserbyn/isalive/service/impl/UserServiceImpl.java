package com.devserbyn.isalive.service.impl;

import com.devserbyn.isalive.model.User;
import com.devserbyn.isalive.model.UserConfig;
import com.devserbyn.isalive.repository.UserConfigRepository;
import com.devserbyn.isalive.service.UserRepositoryService;
import com.devserbyn.isalive.service.UserService;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryService userRepoService;
    private final UserConfigRepository userConfigRepository;

    @Override
    public User register(Update update) {
        User newUser = new User();
        newUser.setChatId(update.getMessage().getChatId());
        newUser.setUsername(update.getMessage().getFrom().getUserName());
        try {
            newUser = userRepoService.save(newUser).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            log.error("User registration failed", e);
        }
        return newUser;
    }

    @Override
    public Optional<User> findByChatID(long chatID) {
        return userRepoService.findByChatId(chatID);
    }

    @Override
    public void remove(Update update) {
        Long userChatId = update.getMessage().getChatId();
        UserConfig userConfig = userConfigRepository.findAll().stream()
                .filter(c -> c.getUser().getChatId() == userChatId)
                .findFirst().orElse(null);
        try {
            if (userConfig != null) {
                userConfigRepository.delete(userConfig);
            }
            if (this.checkIfUserRegistered(userChatId)) {
                userRepoService.deleteByChatId(userChatId);
            }
        } catch (Exception e) {
            userConfigRepository.save(userConfig);
            log.error("User removing failed", e);
        }
    }

    @Override
    public boolean checkIfUserRegistered(long chatId) {
        return userRepoService.findByChatId(chatId).isPresent();
    }
}
