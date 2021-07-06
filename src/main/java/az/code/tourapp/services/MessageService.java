package az.code.tourapp.services;

import az.code.tourapp.configs.BotConfig;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Locale;

@Service
public class MessageService {
    private final Locale locale;
    private final MessageSource messageSource;

    public MessageService(BotConfig botConfig, MessageSource messageSource) {
        this.locale = Locale.forLanguageTag(botConfig.getLang());
        this.messageSource = messageSource;
    }

    public SendMessage staticReplyMessage(long chatId, String message) {
        return new SendMessage(String.valueOf(chatId), messageSource.getMessage(message, null, locale));
    }
    public SendMessage replyMessage(long chatId, String message) {
        return new SendMessage(String.valueOf(chatId), message);
    }
}
