package com.example.telegram_bot_days_to_new_year.inter;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface AnswersInterface {
    void startAnswer(Message command);
    void daysToAnswer();
    void defaultAnswer(Message command);
}
