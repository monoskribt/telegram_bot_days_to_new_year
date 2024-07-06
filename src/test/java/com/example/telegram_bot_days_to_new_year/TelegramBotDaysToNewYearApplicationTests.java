package com.example.telegram_bot_days_to_new_year;


import com.example.telegram_bot_days_to_new_year.config.TelegramBotInitializer;
import com.example.telegram_bot_days_to_new_year.repository.BotUserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TelegramBotDaysToNewYearApplicationTests {
    @MockBean
    TelegramBotInitializer telegramBotInitializer;

    @MockBean
    BotUserRepository botUserRepository;

    @MockBean
    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() {

    }
//    @Test
//    public void testCommandStartForOnePerson() {
//        Update update = mock(Update.class);
//        Message message = mock(Message.class);
//
//        when(update.hasMessage()).thenReturn(true);
//        when(update.getMessage()).thenReturn(message);
//        when(message.hasText()).thenReturn(true);
//        when(message.getText()).thenReturn("/start");
//        when(message.getChatId()).thenReturn(645729166L);
//
//        when(botUserRepository.existsById(645729166L)).thenReturn(true);
//
//        telegramBotController.onUpdateReceived(update);
//
//        verify(botUserRepository, times(1)).existsById(645729166L);
//        verify(telegramBotAnswers, times(1)).startAnswer(645729166L);
//    }

}
