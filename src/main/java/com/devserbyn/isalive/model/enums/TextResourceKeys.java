package com.devserbyn.isalive.model.enums;


public enum TextResourceKeys {

    FIRST_TXT_RES(1),
    SECOND_TXT_RES(2),
    MSG_TYPE_NOT_SUP(3),
    CMD_NOT_AVAILABLE(4),
    CMD_ONLY_SUPPORTED(5),
    SMTH_WENT_WRONG(6),
    LOC_SETUP_2ND_ATTEMPT(7),
    INITIAL_SETUP_FINISHED(8),
    START_2ND_TIME(9),
    ASK_FOR_APP_URL(10),
    BOT_NOT_CONF_YET(13),
    REMOVEME(14),
    HELP_CMD(15),
    REMOVEME_CONFIRM(18),
    INVALID_URL(19),
    YES_NO_INVALID_ANSWER(20),
    ASK_FOR_ISALIVE_URL_SUPPORT(21),
    URL_SAVED_SUCCESS(22),
    URL_NOT_UNIQUE(23),
    ENDPOINT_UP(24),
    ENDPOINT_DOWN(25),
    ENDPOINT_ERROR(26),
    ASK_FOR_REMOVE_ID(27),
    ILLEGAL_REMOVE_NUM(28),
    REMOVE_SUCCESS(29),
    ENDPOINT_RESOLVED(30),
    ENDPOINT_TIMEOUT(31),
    ENDPOINT_ERROR_WITH_INFO(32);

    private final Integer textResourceCode;

    TextResourceKeys(Integer code) {
        this.textResourceCode = code;
    }

    public Integer getTextResourceCode() {
        return textResourceCode;
    }
}
