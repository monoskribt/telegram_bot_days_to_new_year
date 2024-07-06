package com.example.telegram_bot_days_to_new_year.controller_bot;

import com.example.telegram_bot_days_to_new_year.config.TelegramBotConfig;
import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.enums.SubscriptionStatusDaysLeft;
import com.example.telegram_bot_days_to_new_year.inter.HelpersToAnswersInterface;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import com.example.telegram_bot_days_to_new_year.services.TelegramBotAnswers;
import com.example.telegram_bot_days_to_new_year.services.TelegramBotService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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

// maybe it is a bot? but not controller
@Component
public class TelegramBotController
        extends TelegramLongPollingBot
        implements HelpersToAnswersInterface {
    private static final long PERIOD = TimeUnit.SECONDS.toMillis(10);
    private static final long INITIAL_DELAY = TimeUnit.SECONDS.toMillis(10);
    ConcurrentHashMap<Long, LocalDateTime> blockedUser = new ConcurrentHashMap<>();
    @Autowired
    private BotUserRepository repository;
    @Autowired
    private TelegramBotConfig telegramBotConfig;
    @Autowired
    private TelegramBotService telegramBotService;
    @Lazy
    @Autowired
    private TelegramBotAnswers telegramBotAnswers;


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

            if (isUserBlocked(chatId)) {
                telegramBotAnswers.blockedUserMessage(chatId);
                return;
            }

            switch (messageText) {
                case START_BOT:
                    telegramBotAnswers.startAnswer(chatId);
                    break;
                case UNSUBSCRIBE:
                    telegramBotAnswers.unsubscribeAnswer(chatId);
                    break;
                case SUBSCRIBE:
                    telegramBotAnswers.subscribeAnswer(chatId);
                    break;
                case QUIT_FROM_BOT:
                    telegramBotAnswers.quitAnswer(chatId);
                    telegramBotService.deleteUser(chatId);
                    blockedUser.put(chatId, LocalDateTime.now().plusSeconds(10));
                    break;
                default:
                    telegramBotAnswers.defaultAnswer(chatId);
                    break;
            }
        }
    }

    @Override
    public boolean isUserBlocked(Long id) {
        LocalDateTime stillBlocked = blockedUser.get(id);
        if (stillBlocked == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(stillBlocked)) {
            blockedUser.remove(id);
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public void leftDaysToNewYear() {
        long daysTo = helperDaysToNewYear();
        String text = "Days left to New Year: *" + daysTo + "*";

        List<BotUser> usersId = repository.findAll();

        usersId.forEach(user -> {
            if (user.getSubscriptionStatus() == SubscriptionStatusDaysLeft.SUBSCRIBE) {
                telegramBotAnswers.sendMessage(user.getId(), text);
            }
        });
    }

    @Override
    public long helperDaysToNewYear() {
        LocalDate today = LocalDate.now();
        LocalDate nextNewYear = LocalDate.of(today.getYear() + 1, 1, 1);
        return ChronoUnit.DAYS.between(today, nextNewYear);
    }

}