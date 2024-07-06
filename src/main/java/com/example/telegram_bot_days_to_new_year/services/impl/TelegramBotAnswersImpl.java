package com.example.telegram_bot_days_to_new_year.services.impl;

import com.example.telegram_bot_days_to_new_year.enums.SubsStatus;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import com.example.telegram_bot_days_to_new_year.services.TelegramBotAnswers;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static com.example.telegram_bot_days_to_new_year.constants.TextAnswersFromBot.*;

@Service
@RequiredArgsConstructor
public class TelegramBotAnswersImpl implements TelegramBotAnswers {
    @Lazy
    private final AbsSender absSender;

    // you use BotUserRepository in this class and in TelegramBotService it should not be like that
    private final BotUserRepository botUserRepository;
    private final TelegramBotService telegramBotService;

    @Override
    public void startAnswer(Long id) {
        botUserRepository.findById(id)
                .ifPresentOrElse(
                        bu -> {
                            SubsStatus status = bu.getSubscriptionStatus();
                            sendMessage(id, status.equals(SubsStatus.SUBSCRIBE) ?
                                    SUBSCRIBED_ALREADY_TEXT :
                                    UNSUBSCRIBE_WITHOUT_MAIN_PATH_TEXT);
                        },
                        () -> {
                            sendMessage(id, START_TEXT);
                            telegramBotService.addUser(id);
                        }
                );
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
    @Override
    public void sendMessage(Long id, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text(text)
                .build();
        absSender.execute(message);
    }
}
