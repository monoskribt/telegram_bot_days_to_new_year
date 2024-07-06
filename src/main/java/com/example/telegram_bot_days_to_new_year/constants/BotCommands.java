package com.example.telegram_bot_days_to_new_year.constants;

import org.springframework.stereotype.Component;

// Is it Component?
// would it be better to use enums here?
@Component
public class BotCommands {
    public static final String START_BOT = "/start";
    public static final String UNSUBSCRIBE = "/unsubscribe";
    public static final String SUBSCRIBE = "/subscribe";
    public static final String QUIT_FROM_BOT = "/quit";
}
