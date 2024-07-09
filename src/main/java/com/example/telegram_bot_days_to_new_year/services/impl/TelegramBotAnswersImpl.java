package com.example.telegram_bot_days_to_new_year.services.impl;

import com.example.telegram_bot_days_to_new_year.enums.SubsStatus;
import com.example.telegram_bot_days_to_new_year.services.AnswersInterface;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.example.telegram_bot_days_to_new_year.enums.TextAnswersFromBot.*;

@Service
public class TelegramBotAnswersImpl implements AnswersInterface {

    private final TelegramBotService telegramBotService;

    @Autowired
    @Lazy
    private AbsSender absSender;

    public TelegramBotAnswersImpl(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void startAnswer(Long id) {
        if (telegramBotService.isUserExistsWithId(id)) {
            SubsStatus status = telegramBotService.getUserSubscriptionStatus(id);
            sendMessage(id, status.equals(SubsStatus.SUBSCRIBE) ?
                    SUBSCRIBED_ALREADY_TEXT.getText() : UNSUBSCRIBE_WITHOUT_MAIN_PATH_TEXT.getText());
        } else {
            sendMessage(id, START_TEXT.getText());
            telegramBotService.addUser(id);
        }
    }

    @Override
    public void unsubscribeAnswer(Long id) {
        telegramBotService.unsubscribeUser(id);
        sendMessage(id, UNSUBSCRIBE_TEXT.getText());
    }

    @Override
    public void subscribeAnswer(Long id) {
        telegramBotService.subscribeUser(id);
        sendMessage(id, SUBSCRIBE_TEXT.getText());
    }

    @Override
    public void quitAnswer(Long id) {
        sendMessage(id, QUIT_TEXT.getText());
    }

    @Override
    public void defaultAnswer(Long id) {
        sendMessage(id, DEFAULT_TEXT.getText());
    }

    @Override
    public void blockedUserMessage(Long id) {
        sendMessage(id, BLOCKED_USER_TEXT.getText());
    }

    @SneakyThrows
    public void sendMessage(Long id, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text(text)
                .build();
        absSender.execute(message);
    }
}
