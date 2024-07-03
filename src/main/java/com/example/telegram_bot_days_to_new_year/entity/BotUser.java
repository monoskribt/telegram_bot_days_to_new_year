package com.example.telegram_bot_days_to_new_year.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@Table(name = "bot_users")
public class BotUser {

    @Id
    private Long id;

    private boolean userWantToGetInfoAboutDays;

    public BotUser(Long id) {
        this.id = id;
    }

    public BotUser() {

    }

    public void subscribe() {
        this.userWantToGetInfoAboutDays = true;
    }

    public void unsubscribe() {
        this.userWantToGetInfoAboutDays = false;
    }

}