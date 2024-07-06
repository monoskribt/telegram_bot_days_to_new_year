package com.example.telegram_bot_days_to_new_year.controller_bot;

import com.example.telegram_bot_days_to_new_year.config.TelegramBotConfig;
import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.enums.SubsStatus;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import com.example.telegram_bot_days_to_new_year.services.TelegramBotAnswers;
import com.example.telegram_bot_days_to_new_year.services.impl.TelegramBotAnswersImpl;
import com.example.telegram_bot_days_to_new_year.services.impl.TelegramBotService;
import lombok.SneakyThrows;
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
public class TelegramBotController extends TelegramLongPollingBot {
    private static final long PERIOD = TimeUnit.SECONDS.toMillis(10);
    private static final long INITIAL_DELAY = TimeUnit.SECONDS.toMillis(10);
    private final ConcurrentHashMap<Long, LocalDateTime> blockedUser = new ConcurrentHashMap<>();
    private final BotUserRepository repository;
    private final TelegramBotConfig telegramBotConfig;
    private final TelegramBotService telegramBotService;
    private final TelegramBotAnswers telegramBotAnswers;


    // it is not great solution to use deprecated things
    public TelegramBotController(BotUserRepository repository, TelegramBotConfig telegramBotConfig, TelegramBotService telegramBotService, TelegramBotAnswersImpl telegramBotAnswers) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::leftDaysToNewYear, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
        this.repository = repository;
        this.telegramBotConfig = telegramBotConfig;
        this.telegramBotService = telegramBotService;
        this.telegramBotAnswers = telegramBotAnswers;
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

    // do you use this method outside of this class? (private key)
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

    // do you use this method outside of this class? (private key)
    @SneakyThrows
    public void leftDaysToNewYear() {
        long daysTo = helperDaysToNewYear();
        String text = "Days left to New Year: *" + daysTo + "*";

        List<BotUser> usersId = repository.findAll();

        usersId.forEach(user -> {
            if (user.getSubscriptionStatus() == SubsStatus.SUBSCRIBE) {
                telegramBotAnswers.sendMessage(user.getId(), text);
            }
        });
    }

    // do you use this method outside of this class? (private key)
    public long helperDaysToNewYear() {
        LocalDate today = LocalDate.now();
        LocalDate nextNewYear = LocalDate.of(today.getYear() + 1, 1, 1);
        return ChronoUnit.DAYS.between(today, nextNewYear);
    }

}