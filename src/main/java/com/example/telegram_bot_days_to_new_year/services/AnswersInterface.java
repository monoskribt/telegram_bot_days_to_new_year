package com.example.telegram_bot_days_to_new_year.services;

public interface AnswersInterface {
    void startAnswer(Long id);
    void defaultAnswer(Long id);
    void unsubscribeAnswer(Long id);
    void subscribeAnswer(Long id);
    void quitAnswer(Long id);
    void blockedUserMessage(Long id);
}
