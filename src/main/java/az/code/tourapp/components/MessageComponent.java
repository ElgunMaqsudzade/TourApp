package az.code.tourapp.components;

import az.code.tourapp.configs.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class MessageComponent {
    BotConfig botConfig;

    public MessageComponent(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public SendMessage staticReplyMessage(long chatId, String message, Locale locale) {
        ResourceBundle resourceBundle = locale != null
                ? ResourceBundle.getBundle(botConfig.getMessageSource(), locale)
                : ResourceBundle.getBundle(botConfig.getMessageSource());

        return new SendMessage(String.valueOf(chatId), resourceBundle.getString(message));
    }

    public SendMessage replyMessage(long chatId, String message) {
        return new SendMessage(String.valueOf(chatId), message);
    }
}
