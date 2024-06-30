package com.example.telegram_bot_days_to_new_year;

import com.example.telegram_bot_days_to_new_year.bot.TelegramBotController;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TelegramBotDaysToNewYearApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotDaysToNewYearApplication.class, args);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBotController());
    }
}
