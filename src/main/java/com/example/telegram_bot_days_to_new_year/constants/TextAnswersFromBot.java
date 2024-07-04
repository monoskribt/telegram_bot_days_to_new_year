package com.example.telegram_bot_days_to_new_year.constants;

public class TextAnswersFromBot {
    public static final String START_TEXT = "Hello, dear user! " +
            "Everyday at 8:00 AM i gonna be inform you as for left days to New Year";

    public static final String SUBSCRIBE_TEXT =
            "You will be again get information about the left days to New Year.";

    public static final String SUBSCRIBED_ALREADY_TEXT =
            "You are getting information as for left days to New Year already.";

    public static final String UNSUBSCRIBE_TEXT = "You will not get more information as for days left to New Year. " +
            "If u want get information as for days again - enter command: " + BotCommands.SUBSCRIBE;

    public static final String UNSUBSCRIBE_WITHOUT_MAIN_PATH_TEXT =
            "If u want get information as for days again - enter command: " + BotCommands.SUBSCRIBE;

    public static final String QUIT_TEXT = "You are decided quit chat. " +
            "If u will be want connect to chat again - you could be do this after 1 hours.";

    public static final String DEFAULT_TEXT = "Likely you entered the wrong command. \n" +
            "Please, try again. Here are my commands: \n" +
            BotCommands.START_BOT + "\n" +
            BotCommands.UNSUBSCRIBE + "\n" +
            BotCommands.SUBSCRIBE + "\n" +
            BotCommands.QUIT_FROM_BOT;

    public static final String BLOCKED_USER_TEXT =
            "You are temporarily blocked from rejoining the chat. Please try again later.";
}
