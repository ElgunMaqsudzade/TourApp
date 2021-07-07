package az.code.tourapp;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@Setter
@Slf4j
public class MessageComponent extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    public void sendMessage(Long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(String.valueOf(chatId)).text(text).build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
