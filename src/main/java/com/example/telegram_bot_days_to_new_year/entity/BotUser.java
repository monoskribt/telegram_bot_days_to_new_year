package com.example.telegram_bot_days_to_new_year.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@Table(name = "bot_users")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean userWantToGetInfoAboutDays;

    public BotUser(Long id, boolean userWantToGetInfoAboutDays) {
        this.id = id;
        this.userWantToGetInfoAboutDays = userWantToGetInfoAboutDays;
    }

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
