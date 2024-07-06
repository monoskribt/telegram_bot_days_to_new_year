package com.example.telegram_bot_days_to_new_year.services;

import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.enums.SubscriptionStatusDaysLeft;
import com.example.telegram_bot_days_to_new_year.inter.AnswersInterface;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.example.telegram_bot_days_to_new_year.constants.TextAnswersFromBot.*;

@Service
public class TelegramBotAnswers implements AnswersInterface {

    @Lazy
    @Autowired
    private AbsSender absSender;

    @Autowired
    private BotUserRepository botUserRepository;


    @Autowired
    private TelegramBotService telegramBotService;

    @Override
    public void startAnswer(Long id) {
        BotUser botUser = botUserRepository.findById(id).orElse(null);

        if (telegramBotService.getUserWithId(id) &&
            botUser.getSubscriptionStatus() == SubscriptionStatusDaysLeft.SUBSCRIBE) {
            sendMessage(id, SUBSCRIBED_ALREADY_TEXT);
        } else if (telegramBotService.getUserWithId(id) &&
                   botUser.getSubscriptionStatus() == SubscriptionStatusDaysLeft.UNSUBSCRIBE) {
            sendMessage(id, UNSUBSCRIBE_WITHOUT_MAIN_PATH_TEXT);
        } else {
            sendMessage(id, START_TEXT);

            telegramBotService.addUser(id);
        }
    }

    @SneakyThrows
    @Override
    public void unsubscribeAnswer(Long id) {
        telegramBotService.unsubscribeUser(id);
        sendMessage(id, UNSUBSCRIBE_TEXT);
    }

    @SneakyThrows
    @Override
    public void subscribeAnswer(Long id) {
        telegramBotService.subscribeUser(id);
        sendMessage(id, SUBSCRIBE_TEXT);
    }

    @SneakyThrows
    @Override
    public void quitAnswer(Long id) {
        sendMessage(id, QUIT_TEXT);
    }

    @SneakyThrows
    @Override
    public void defaultAnswer(Long id) {
        sendMessage(id, DEFAULT_TEXT);
    }

    @SneakyThrows
    @Override
    public void blockedUserMessage(Long id) {
        sendMessage(id, BLOCKED_USER_TEXT);
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
