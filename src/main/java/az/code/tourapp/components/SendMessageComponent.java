package az.code.tourapp.components;

import az.code.tourapp.configs.BotConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Getter
@Setter
@Slf4j
@Component
public class SendMessageComponent extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    BotConfig config;

    public SendMessageComponent(BotConfig config) {
        this.config = config;
        this.botPath = config.getPath();
        this.botUsername = config.getUsername();
        this.botToken = config.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }


    public <T extends BotApiMethod<Message>> void sendMessage(T message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
    public <T extends BotApiMethod<Serializable>> void sendEditedMessage(T message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
    public void editMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText new_message = EditMessageText
                .builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .replyMarkup(keyboard)
                .build();
        sendEditedMessage(new_message);
    }
}