package az.code.tourapp.components.interfaces;


import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;


public interface InputMessageHandler {
    BotApiMethod<?> handle(Message message);

    String getMainState();
}
