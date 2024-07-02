package com.example.telegram_bot_days_to_new_year;


import com.example.telegram_bot_days_to_new_year.controller_bot.TelegramBotController;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


import static org.mockito.Mockito.*;

@SpringBootTest
class TelegramBotDaysToNewYearApplicationTests {

    //Currently not working correctly

    @Mock
    private BotUserRepository botUserRepository;

    @InjectMocks
    private TelegramBotController telegramBotController;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCommandStartForOnePerson() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/start");
        when(message.getChatId()).thenReturn(645729166L);

        when(botUserRepository.existsById(645729166L)).thenReturn(true);

        telegramBotController.onUpdateReceived(update);

        verify(botUserRepository, times(1)).existsById(645729166L);
        verify(telegramBotController, times(1)).startAnswer(645729166L);
    }

}
