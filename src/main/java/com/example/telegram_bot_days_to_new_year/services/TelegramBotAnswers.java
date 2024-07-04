package com.example.telegram_bot_days_to_new_year.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
public class TelegramBotAnswers {

    @Autowired
    private AbsSender absSender;


}
