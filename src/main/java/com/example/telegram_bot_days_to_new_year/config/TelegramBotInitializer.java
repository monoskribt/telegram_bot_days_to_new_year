package com.example.telegram_bot_days_to_new_year.config;

import com.example.telegram_bot_days_to_new_year.controller_bot.TelegramBotController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Component
public class TelegramBotInitializer {
    private final TelegramBotController telegramBotController;

    @Autowired
    public TelegramBotInitializer(TelegramBotController telegramBotController) {
        this.telegramBotController = telegramBotController;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init()throws TelegramApiException{
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(telegramBotController);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
