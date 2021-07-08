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

    public SendMessage replyMessage(long chatId, String message) {
        return new SendMessage(String.valueOf(chatId), message);
    }
}
