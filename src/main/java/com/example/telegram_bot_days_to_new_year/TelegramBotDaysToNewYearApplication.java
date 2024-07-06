package com.example.telegram_bot_days_to_new_year;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TelegramBotDaysToNewYearApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TelegramBotDaysToNewYearApplication.class, args);
    }
}