package com.example.telegram_bot_days_to_new_year.bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TelegramBotController extends TelegramLongPollingBot {
    public final static String START_BOT = "/start";
    public final static String DAYS_TO_NEW_YEAR = "/daysLeft";

    @Override
    public String getBotUsername() {
        return "days_to_new_year_bot";
    }
    @Override
    public String getBotToken() {
        return "7460819983:AAGqRM0tJiag854hFLk5Rc-TFYTbaBjwBpU";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String messageText;
        Message command;

        if(update.hasMessage() && update.getMessage().hasText()) {
            messageText = update.getMessage().getText();
            command = update.getMessage();

            switch (messageText) {
                case START_BOT:
                    startAnswer(command);
                    break;
                case DAYS_TO_NEW_YEAR:
                    daysToAnswer(command);
                    break;
                default:
                    defaultAnswer(command);
                    break;
            }
        }
    }

    @SneakyThrows
    public void startAnswer(Message command) {
        SendMessage message = SendMessage.builder()
                .chatId(command.getChatId())
                .parseMode("Markdown")
                .text("Hello, user! I can help you find out how many days left to New Year." +
                        " To do this, write command: /daysLeft")
                .build();
        execute(message);
    }


    @SneakyThrows
    public void daysToAnswer(Message command) {
        long daysTo = helperDaysToNewYear();
        String text = "Days left to New Year: *" + daysTo + "*";

        SendMessage message = SendMessage.builder()
                .chatId(command.getChatId())
                .parseMode("Markdown")
                .text(text)
                .build();
        execute(message);
    }

    @SneakyThrows
    public void defaultAnswer(Message command) {
        String wrongAnswer = "Likely to you entered the wrong command. " +
                "Please, try write again. There are my commands:\n" +
                "/start\n" +
                "/daysLeft";

        SendMessage message = SendMessage.builder()
                .chatId(command.getChatId())
                .parseMode("Markdown")
                .text(wrongAnswer)
                .build();
        execute(message);
    }

    public long helperDaysToNewYear() {
        LocalDate today = LocalDate.now();
        LocalDate nextNewYear = LocalDate.of(today.getYear() + 1, 1, 1);
        return ChronoUnit.DAYS.between(today, nextNewYear);
    }
}
