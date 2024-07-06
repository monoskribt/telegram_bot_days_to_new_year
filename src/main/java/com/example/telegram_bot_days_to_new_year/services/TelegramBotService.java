package com.example.telegram_bot_days_to_new_year.services;

import com.example.telegram_bot_days_to_new_year.entity.BotUser;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TelegramBotService {
    BotUserRepository botUserRepository;

    public void addUser(Long id) {
        if (!botUserRepository.existsById(id)) {
            botUserRepository.save(new BotUser(id));
        }
    }

    public void deleteUser(Long id) {
        Optional<BotUser> botUserList = botUserRepository.findById(id);
        if (botUserList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        botUserRepository.deleteById(id);
    }

    public void subscribeUser(Long id) {
        BotUser user = botUserRepository.findById(id).orElse(null);
        if (user != null) {
            user.subscribe();
            botUserRepository.save(user);
        }
    }

    public void unsubscribeUser(Long id) {
        BotUser user = botUserRepository.findById(id).orElse(null);
        if (user != null) {
            user.unsubscribe();
            botUserRepository.save(user);
        }
    }

    public boolean getUserWithId(Long id) {
        BotUser botUser = botUserRepository.findById(id).orElse(null);
        if (botUser == null) {
            return false;
        }
        return true;
    }
}
