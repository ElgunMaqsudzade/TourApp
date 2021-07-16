package az.code.tourapp.components;

import az.code.tourapp.components.interfaces.StateHandler;
import az.code.tourapp.exceptions.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class BotStateContext {
    private final Map<String, StateHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<StateHandler> mHandlers) {
        mHandlers.forEach(handler -> this.messageHandlers.put(handler.getMainState(), handler));
    }


    public BotApiMethod<?> processInputMessage(String mainState, Message message) {
        StateHandler stateHandler = messageHandlers.get(mainState);

        if (stateHandler == null) throw new NotFound("Message Handler for state not found");

        return stateHandler.handleMessage(message);
    }

    public BotApiMethod<?> processCallBack(String mainState, CallbackQuery query) {
        StateHandler stateHandler = messageHandlers.get(mainState);

        if (stateHandler == null) throw new NotFound("Callback Handler for state not found");

        return stateHandler.handleCallBack(query);
    }
}





