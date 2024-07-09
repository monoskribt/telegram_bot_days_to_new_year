package com.example.telegram_bot_days_to_new_year.enums;

import com.example.telegram_bot_days_to_new_year.constants.BotCommands;
import lombok.Getter;

@Getter
public enum TextAnswersFromBot {
    START_TEXT("Hello, dear user! Everyday at 8:00 AM I gonna inform you about the left days to New Year."),

    SUBSCRIBE_TEXT("You will be again getting information about the left days to New Year."),

    SUBSCRIBED_ALREADY_TEXT("You are already getting information about the left days to New Year."),

    UNSUBSCRIBE_TEXT("You will not get more information about the days left to New Year." +
            "If you want to get information about the days again - enter the command: " + BotCommands.SUBSCRIBE),

    UNSUBSCRIBE_WITHOUT_MAIN_PATH_TEXT("If you want to get information about the days again - enter the command: "
            + BotCommands.SUBSCRIBE),

    QUIT_TEXT("You have decided to quit the chat. " +
            "If you want to connect to the chat again - you can do this after 1 hour."),

    DEFAULT_TEXT("Likely you entered the wrong command. \nPlease, try again. Here are my commands: \n" +
            BotCommands.START_BOT + "\n" +
            BotCommands.UNSUBSCRIBE + "\n" +
            BotCommands.SUBSCRIBE + "\n" +
            BotCommands.QUIT_FROM_BOT),

    BLOCKED_USER_TEXT("You are temporarily blocked from rejoining the chat. Please try again later.");

    private final String text;

    TextAnswersFromBot(String text) {
        this.text = text;
    }

}
