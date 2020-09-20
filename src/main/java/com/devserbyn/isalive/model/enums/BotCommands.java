package com.devserbyn.isalive.model.enums;

public enum BotCommands {

    NOT_EXISTING(""),
    START("/start"),
    ADD("/add"),
    LIST("/list"),
    REMOVE("/remove"),
    REMOVEME("/removeme"),
    HELP("/help");

    private final String command;

    BotCommands(String command) {
        this.command = command;
    }

    public final String getCommand() {
        return command;
    }
}
