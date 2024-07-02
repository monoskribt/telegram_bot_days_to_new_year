package com.example.telegram_bot_days_to_new_year.controller_bot;
import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.inter.AnswersInterface;
import com.example.telegram_bot_days_to_new_year.inter.BotCommandsInterface;
import com.example.telegram_bot_days_to_new_year.inter.HelpersToAnswersInterface;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public class TelegramBotController
        extends TelegramLongPollingBot
        implements AnswersInterface, HelpersToAnswersInterface, BotCommandsInterface
{
    final BotUserRepository repository;


    public TelegramBotController(BotUserRepository repository) {
        long initialDelay = computeDelaySendInfo();
        long period = TimeUnit.SECONDS.toMillis(10);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::leftDaysToNewYear, initialDelay, period, TimeUnit.MILLISECONDS);
        this.repository = repository;
    }



    @Override
    public String getBotUsername() {
        return "days_to_new_year_bot";
    }

    @Override
    public String getBotToken() {
        return "7460819983:AAF7ruzjnJ61IkGom4w6cm_3E2VYZWPlh8I";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();

            switch (messageText) {
                case START_BOT:
                    if (!repository.existsById(chatId)) {
                        repository.save(new BotUser(chatId));
                    }
                    else {
                        BotUser user = repository.findById(chatId).orElse(null);
                        if(user != null) {
                            user.subscribe();
                            repository.save(user);
                        }
                    }
                    startAnswer(chatId);
                    break;
                case STOP_BOT:
                    stopAnswer(chatId);
                    break;
                default:
                    defaultAnswer(chatId);
                    break;
            }
        }
    }



    @SneakyThrows
    public void startAnswer(Long id) {
        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text("Hello, dear user! " +
                        "Everyday at 8:00 AM i gonna be inform you as for left days to New Year")
                .build();
        execute(message);
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
    public void defaultAnswer(Long id) {
        String wrongAnswer = """
                Likely you entered the wrong command. 
                Please, try again. Here are my commands:
                /start""";

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

    @SneakyThrows
    @Override
    public void stopAnswer(Long id) {
        BotUser user = repository.findById(id).orElse(null);
        if (user != null) {
            user.unsubscribe();
            repository.save(user);
        }

        SendMessage message = SendMessage.builder()
                .chatId(id.toString())
                .parseMode("Markdown")
                .text("You will not get more information as for days left to New Year. " +
                        "If u want get information as for days again - enter command *" + START_BOT + "*")
                .build();
        execute(message);
    }
}
