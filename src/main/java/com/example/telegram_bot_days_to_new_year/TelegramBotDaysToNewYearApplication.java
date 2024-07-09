package com.example.telegram_bot_days_to_new_year;

import com.example.telegram_bot_days_to_new_year.props.TelegramBotProps;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableConfigurationProperties(TelegramBotProps.class)
@EnableScheduling
public class TelegramBotDaysToNewYearApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotDaysToNewYearApplication.class, args);
    }
}