package az.code.tourapp.components;

import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.exceptions.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class BotStateContext {
    private final Map<String, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {

        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getMainState(), handler));
    }

    public SendMessage processInputMessage(String mainState, Message message) {
        InputMessageHandler messageHandler = messageHandlers.get(mainState);
        if (messageHandler == null) throw new NotFound("Handler for state not found");

        return messageHandler.handle(message);

    }
}





