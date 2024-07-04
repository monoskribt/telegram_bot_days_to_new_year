package com.example.telegram_bot_days_to_new_year.entity;

import com.example.telegram_bot_days_to_new_year.enums.SubscriptionStatusDaysLeft;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@Table(name = "bot_users")
public class BotUser {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'SUBSCRIBE'")
    private SubscriptionStatusDaysLeft subscriptionStatus;

    public BotUser(Long id) {
        this.id = id;
        this.subscriptionStatus = SubscriptionStatusDaysLeft.SUBSCRIBE;
    }

    public BotUser() {
        this.subscriptionStatus = SubscriptionStatusDaysLeft.SUBSCRIBE;
    }

    public void subscribe() {
        this.subscriptionStatus = SubscriptionStatusDaysLeft.SUBSCRIBE;
    }

    public void unsubscribe() {
        this.subscriptionStatus = SubscriptionStatusDaysLeft.UNSUBSCRIBE;
    }

}