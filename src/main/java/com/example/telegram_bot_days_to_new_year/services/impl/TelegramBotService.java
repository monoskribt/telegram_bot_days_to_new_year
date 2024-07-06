package com.example.telegram_bot_days_to_new_year.services.impl;

import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final BotUserRepository botUserRepository;

    public void addUser(Long id) {
        if (!botUserRepository.existsById(id)) {
            botUserRepository.save(new BotUser(id));
        }
    }

    public void deleteUser(Long id) {
        botUserRepository.findById(id)
                .ifPresent(botUserRepository::delete);
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

    // this method should return user by id, in this case it should be isUserExistsWithId
    public boolean getUserWithId(Long id) {
        return botUserRepository.findById(id).isPresent();
    }
}
