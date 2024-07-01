package com.example.telegram_bot_days_to_new_year.controller_bot;
import com.example.telegram_bot_days_to_new_year.inter.AnswersInterface;
import com.example.telegram_bot_days_to_new_year.inter.BotCommandsInterface;
import com.example.telegram_bot_days_to_new_year.inter.HelpersToAnswersInterface;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TelegramBotController
        extends TelegramLongPollingBot
        implements AnswersInterface, HelpersToAnswersInterface, BotCommandsInterface
{

    private final Set<Long> chatIds = new HashSet<>();


    public TelegramBotController() {
        long initialDelay = computeDelaySendInfo();
        long period = TimeUnit.SECONDS.toMillis(10);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::daysToAnswer, initialDelay, period, TimeUnit.MILLISECONDS);
    }


    @Override
    public String getBotUsername() {
        return "days_to_new_year_bot";
    }

    @Override
    public String getBotToken() {
        return "7460819983:AAEFljtlXCMCwIxy3VbtKWrNrPyjjsg0Q_4";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();

            chatIds.add(chatId);

            switch (messageText) {
                case START_BOT:
                    startAnswer(message);
                    break;
                case DAYS_TO_NEW_YEAR:
                    daysToAnswer();
                    break;
                default:
                    defaultAnswer(message);
                    break;
            }
        }
    }



    @SneakyThrows
    public void startAnswer(Message command) {
        SendMessage message = SendMessage.builder()
                .chatId(command.getChatId().toString())
                .parseMode("Markdown")
                .text("Hello, user! I can help you find out how many days left to New Year. To do this, write command: /daysLeft")
                .build();
        execute(message);
    }

    @SneakyThrows
    public void daysToAnswer() {
        long daysTo = helperDaysToNewYear();
        String text = "Days left to New Year: *" + daysTo + "*";

        for(Long chatId : chatIds) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .parseMode("Markdown")
                    .text(text)
                    .build();
            execute(message);
        }
    }

    @SneakyThrows
    public void defaultAnswer(Message command) {
        String wrongAnswer = """
                Likely you entered the wrong command. 
                Please, try again. Here are my commands:
                /start
                /daysLeft""";

        SendMessage message = SendMessage.builder()
                .chatId(command.getChatId().toString())
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
