package com.example.telegram_bot_days_to_new_year.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// do you actually need it?
//@Configuration
//@Data
//@PropertySource("application.properties")

// please move this file to props folder and name it like TelegramBotProps (you can use @ConfigurationProperties with id to your property set)
public class TelegramBotConfig {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
}


