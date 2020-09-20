package com.devserbyn.isalive.model;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class UserSetupStepsBO {

    private List<UserSetupSteps> userSetupStepsList = new ArrayList<>();

    public void removeByChatId(long chatId) {
        for (int i = 0; i < userSetupStepsList.size(); i++) {
            if (userSetupStepsList.get(i).getChatId() == chatId) {
                userSetupStepsList.remove(i);
                break;
            }
        }
    }

    public Optional<UserSetupSteps> getUserSetupStepsByChatId(long chatID) {
        return userSetupStepsList.stream().filter(uss -> uss.getChatId() == chatID).findFirst();
    }

    public UserSetupSteps createNewSetupSteps(long chatId) {
        UserSetupSteps newSteps = new UserSetupSteps(chatId);
        this.userSetupStepsList.add(newSteps);
        return newSteps;
    }

    public UserSetupSteps getOrCreateUserSetupStepsByChatId(long chatID) {
        return this.userSetupStepsList.stream().filter(steps -> steps.getChatId() == chatID).findFirst()
                .orElse(createNewSetupSteps(chatID));
    }

    @Getter
    @Setter
    public static class UserSetupSteps {

        public UserSetupSteps() { }

        public UserSetupSteps(long chatId) {
            this.chatId = chatId;
        }

        private long chatId;
        private boolean waitingForAppURL;
        private boolean waitingForSupportsIsAlive;
        private boolean waitingForRemoveID;
        private LocalDateTime removemeSentTime = LocalDateTime.of(1999, 1, 1, 1, 1);
    }
}
