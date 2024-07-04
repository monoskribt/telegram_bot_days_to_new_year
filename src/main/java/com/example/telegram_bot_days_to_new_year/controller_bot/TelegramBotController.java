package com.example.telegram_bot_days_to_new_year.controller_bot;
import com.example.telegram_bot_days_to_new_year.config.TelegramBotConfig;
import com.example.telegram_bot_days_to_new_year.constants.BotCommands;
import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.enums.SubscriptionStatusDaysLeft;
import com.example.telegram_bot_days_to_new_year.inter.AnswersInterface;
import com.example.telegram_bot_days_to_new_year.inter.HelpersToAnswersInterface;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import com.example.telegram_bot_days_to_new_year.services.TelegramBotService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.telegram_bot_days_to_new_year.constants.BotCommands.*;
import static com.example.telegram_bot_days_to_new_year.constants.TextAnswersFromBot.*;

@Component
public class TelegramBotController
        extends TelegramLongPollingBot
        implements AnswersInterface, HelpersToAnswersInterface
{
    private static final long PERIOD = TimeUnit.SECONDS.toMillis(10);
    private static final long INITIAL_DELAY = TimeUnit.SECONDS.toMillis(10);

    @Autowired
    private BotUserRepository repository;

    @Autowired
    private TelegramBotConfig telegramBotConfig;

    @Autowired
    private TelegramBotService telegramBotService;

    ConcurrentHashMap<Long, LocalDateTime> blockedUser = new ConcurrentHashMap<>();


    public TelegramBotController() {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::leftDaysToNewYear, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }



    @Override
    public String getBotUsername() {
        return telegramBotConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getBotToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();

            if(isUserBlocked(chatId)) {
                blockedUserMessage(chatId);
                return;
            }

            switch (messageText) {
                case START_BOT:
                    startAnswer(chatId);
                    break;
                case UNSUBSCRIBE:
                    unsubscribeAnswer(chatId);
                    break;
                case SUBSCRIBE:
                    subscribeAnswer(chatId);
                    break;
                case QUIT_FROM_BOT:
                    quitAnswer(chatId);
                    break;
                default:
                    defaultAnswer(chatId);
                    break;
            }
        }
    }




    @SneakyThrows
    public void startAnswer(Long id) {
        BotUser botUser = repository.findById(id).orElse(null);

        if(telegramBotService.getUserWithId(id) &&
                botUser.getSubscriptionStatus() == SubscriptionStatusDaysLeft.SUBSCRIBE) {
            sendMessage(id, SUBSCRIBED_ALREADY_TEXT);
        }

        else if (telegramBotService.getUserWithId(id) &&
                botUser.getSubscriptionStatus() == SubscriptionStatusDaysLeft.UNSUBSCRIBE)
        {
            sendMessage(id, UNSUBSCRIBE_WITHOUT_MAIN_PATH_TEXT);
        }

        else {
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

        telegramBotService.deleteUser(id);

        blockedUser.put(id, LocalDateTime.now().plusSeconds(10));
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
    public void leftDaysToNewYear() {
        long daysTo = helperDaysToNewYear();
        String text = "Days left to New Year: *" + daysTo + "*";

        List<BotUser> usersId = repository.findAll();

        for(BotUser chatId : usersId) {
            if(chatId.getSubscriptionStatus() == SubscriptionStatusDaysLeft.SUBSCRIBE) {
                sendMessage(chatId.getId(), text);
            }
        }
    }

    @Override
    public boolean isUserBlocked(Long id) {
        LocalDateTime stillBlocked = blockedUser.get(id);
        if(stillBlocked == null) {
            return false;
        }

        if(LocalDateTime.now().isAfter(stillBlocked)) {
            blockedUser.remove(id);
            return false;
        }
        return true;
    }

    public long helperDaysToNewYear() {
        LocalDate today = LocalDate.now();
        LocalDate nextNewYear = LocalDate.of(today.getYear() + 1, 1, 1);
        return ChronoUnit.DAYS.between(today, nextNewYear);
    }

    @SneakyThrows
    public void sendMessage(Long id, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text(text)
                .build();
        execute(message);
    }
}