package az.code.tourapp.components;

import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.dtos.BotState;
import az.code.tourapp.exceptions.NotFound;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {

        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getMainState(), handler));
    }

    public SendMessage processInputMessage(BotState mainState, Message message) {
        try {
            InputMessageHandler messageHandler = messageHandlers.get(mainState);
            return messageHandler.handle(message);
        } catch (Exception ex){
            throw new NotFound("Handler for state not found");
        }
    }
}





