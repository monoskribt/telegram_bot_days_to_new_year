package com.example.telegram_bot_days_to_new_year.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class TelegramBotProps {
    private String name;
    private String token;
}
