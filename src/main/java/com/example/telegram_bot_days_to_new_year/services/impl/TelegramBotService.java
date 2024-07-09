package com.example.telegram_bot_days_to_new_year.services.impl;

import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.enums.SubsStatus;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final BotUserRepository botUserRepository;

    public void addUser(Long id) {
        if(!botUserRepository.existsById(id)) {
            botUserRepository.save(new BotUser(id));
        }
    }

    public void deleteUser(Long id) {
        botUserRepository.findById(id).ifPresent(botUserRepository::delete);
    }

    public void subscribeUser(Long id) {
        botUserRepository.findById(id)
                .ifPresent(user -> {
                    user.subscribe();
                    botUserRepository.save(user);
                });
    }

    public void unsubscribeUser(Long id) {
        botUserRepository.findById(id)
                .ifPresent(user -> {
                    user.unsubscribe();
                    botUserRepository.save(user);
                });
    }

    public boolean isUserExistsWithId(Long id) {
        return botUserRepository.existsById(id);
    }

    public List<BotUser> findAllUsersWithSubscribeStatus() {
        return botUserRepository.findAll().stream()
                .filter(user -> user.getSubscriptionStatus().equals(SubsStatus.SUBSCRIBE))
                .collect(Collectors.toList());
    }

    public SubsStatus getUserSubscriptionStatus(Long id) {
        return botUserRepository.findById(id)
                .map(BotUser::getSubscriptionStatus)
                .orElse(null);
    }
}
