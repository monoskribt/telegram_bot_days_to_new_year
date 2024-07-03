package com.example.telegram_bot_days_to_new_year.controller_bot;
import com.example.telegram_bot_days_to_new_year.config.TelegramBotConfig;
import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.inter.AnswersInterface;
import com.example.telegram_bot_days_to_new_year.inter.BotCommandsInterface;
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


@Component
public class TelegramBotController
        extends TelegramLongPollingBot
        implements AnswersInterface, HelpersToAnswersInterface, BotCommandsInterface
{
    @Autowired
    private BotUserRepository repository;

    private final TelegramBotConfig telegramBotConfig;

    @Autowired
    private TelegramBotService telegramBotService;

    ConcurrentHashMap<Long, LocalDateTime> blockedUser = new ConcurrentHashMap<>();


    public TelegramBotController(TelegramBotConfig telegramBotConfig) {
        long initialDelay = computeDelaySendInfo();
        long period = TimeUnit.SECONDS.toMillis(10);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::leftDaysToNewYear, initialDelay, period, TimeUnit.MILLISECONDS);

        this.telegramBotConfig = telegramBotConfig;
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
                case STOP_BOT:
                    unsubscribeAnswer(chatId);
                    break;
                case UPDATE_BOT:
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

        if(telegramBotService.getUserWithId(id) && botUser.isUserWantToGetInfoAboutDays()) {
            SendMessage message = SendMessage.builder()
                    .chatId(id.toString())
                    .parseMode("Markdown")
                    .text("You are getting information as for left days to New Year already.")
                    .build();
            execute(message);
        }

        else if (telegramBotService.getUserWithId(id) && !botUser.isUserWantToGetInfoAboutDays()) {
            SendMessage message = SendMessage.builder()
                    .chatId(id.toString())
                    .parseMode("Markdown")
                    .text("If u want get information as for days again - enter command: " + UPDATE_BOT)
                    .build();
            execute(message);
        }

        else {
            SendMessage message = SendMessage.builder()
                    .chatId(id.toString())
                    .parseMode("Markdown")
                    .text("Hello, dear user! " +
                            "Everyday at 8:00 AM i gonna be inform you as for left days to New Year")
                    .build();
            execute(message);

            telegramBotService.addUser(id);
        }
    }

    @SneakyThrows
    public void leftDaysToNewYear() {
        long daysTo = helperDaysToNewYear();
        String text = "Days left to New Year: *" + daysTo + "*";

        List<BotUser> usersId = repository.findAll();

        for(BotUser chatId : usersId) {
            if(chatId.isUserWantToGetInfoAboutDays()) {
                SendMessage message = SendMessage.builder()
                        .chatId(chatId.getId().toString())
                        .parseMode("Markdown")
                        .text(text)
                        .build();
                execute(message);
            }
        }
    }

    @SneakyThrows
    @Override
    public void unsubscribeAnswer(Long id) {
        telegramBotService.unsubscribeUser(id);

        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text("You will not get more information as for days left to New Year. " +
                        "If u want get information as for days again - enter command *" + UPDATE_BOT + "*")
                .build();
        execute(message);
    }

    @SneakyThrows
    @Override
    public void subscribeAnswer(Long id) {
        telegramBotService.subscribeUser(id);

        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text("You are going to get information as for left days to New Year again")
                .build();
        execute(message);
    }

    @SneakyThrows
    @Override
    public void quitAnswer(Long id) {
        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text("You are decide quit chat. " +
                        "If u will be want connect to chat again - you could be do this after 1 hours.")
                .build();
        execute(message);

        telegramBotService.deleteUser(id);

        blockedUser.put(id, LocalDateTime.now().plusSeconds(10));
    }

    @SneakyThrows
    @Override
    public void blockedUserMessage(Long id) {
        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text("You are temporarily blocked from rejoining the chat. Please try again later.")
                .build();
        execute(message);
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

    @SneakyThrows
    @Override
    public void defaultAnswer(Long id) {
        String wrongAnswer = "Likely you entered the wrong command. \n" +
                "Please, try again. Here are my commands: \n" +
                START_BOT + "\n" +
                STOP_BOT + "\n" +
                UPDATE_BOT + "\n" +
                QUIT_FROM_BOT;

        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
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

    public long computeDelaySendInfo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextTime = now.plusSeconds(10);
        return ChronoUnit.MILLIS.between(now, nextTime);
    }
}