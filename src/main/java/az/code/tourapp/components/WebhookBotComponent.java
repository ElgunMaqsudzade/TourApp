package az.code.tourapp.components;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.components.KeyboardContext;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.dtos.KeyboardDTO;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.models.ActionInput;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.Offer;
import az.code.tourapp.models.Reply;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.SubCacheService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
@Component
public class WebhookBotComponent extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    private KeyboardContext context;

    public WebhookBotComponent(KeyboardContext context, BotConfig config) {
        this.botPath = config.getPath();
        this.botUsername = config.getUsername();
        this.botToken = config.getToken();
        this.context = context;
    }

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

    public void sendAction(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendEditedMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        try {
            EditMessageText new_message = EditMessageText
                    .builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            execute(new_message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }


    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            execute(DeleteMessage.builder().messageId(messageId).chatId(String.valueOf(chatId)).build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    public Message sendPhoto(SendPhoto photo) {
        try {
            return execute(photo);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void sendMediaGroup(SendMediaGroup message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
