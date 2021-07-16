package az.code.tourapp.components.interfaces;


import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;


public interface StateHandler {
    BotApiMethod<?> handleMessage(Message message);
    BotApiMethod<?> handleCallBack(CallbackQuery query);

    String getMainState();
}
