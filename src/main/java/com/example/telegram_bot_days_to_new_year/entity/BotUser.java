package com.example.telegram_bot_days_to_new_year.entity;

import com.example.telegram_bot_days_to_new_year.enums.SubsStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bot_users")
public class BotUser {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'SUBSCRIBE'")
    private SubsStatus subscriptionStatus;

    public BotUser(Long id) {
        this.id = id;
        this.subscriptionStatus = SubsStatus.SUBSCRIBE;
    }

    public BotUser() {
        this.subscriptionStatus = SubsStatus.SUBSCRIBE;
    }

    public void subscribe() {
        this.subscriptionStatus = SubsStatus.SUBSCRIBE;
    }

    public void unsubscribe() {
        this.subscriptionStatus = SubsStatus.UNSUBSCRIBE;
    }

}