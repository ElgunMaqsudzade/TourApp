package az.code.tourapp.components.messagehandlers;

import az.code.tourapp.components.interfaces.StateHandler;
import az.code.tourapp.models.enums.BasicState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;



@Slf4j
@Component
public class IdleHandler implements StateHandler {

    @Override
    public BotApiMethod<?> handleMessage(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Integer message_id = message.getMessageId();

        System.out.println(usersAnswer);

        return null;
    }

    @Override
    public BotApiMethod<?> handleCallBack(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        long userId = query.getFrom().getId();
        Integer message_id = query.getMessage().getMessageId();
        String usersAnswer = query.getData();

        System.out.println(usersAnswer);

        return null;
    }

    @Override
    public String getMainState() {
        return BasicState.IDLE.toString();
    }
}
