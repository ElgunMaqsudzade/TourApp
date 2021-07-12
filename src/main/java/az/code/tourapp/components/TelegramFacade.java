package az.code.tourapp.components;

import az.code.tourapp.components.statehandlers.CallBackHandler;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.enums.BasicState;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.services.AppUserCacheService;
import az.code.tourapp.utils.EnumUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class TelegramFacade {
    AppUserCacheService cache;
    BotStateContext stateContext;
    CallBackHandler callBackHandler;
    AppUserDAO appUserDAO;

    private final List<String> IGNORE;


    public TelegramFacade(AppUserCacheService cache, BotStateContext stateContext, CallBackHandler callBackHandler, AppUserDAO appUserDAO, BotConfig config) {
        this.cache = cache;
        this.stateContext = stateContext;
        this.callBackHandler = callBackHandler;
        this.appUserDAO = appUserDAO;
        this.IGNORE = config.getIgnore().getHard();
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        BotApiMethod<?> replyMessage = null;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    update.getCallbackQuery().getFrom().getFirstName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return callBackHandler.processQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getFirstName(), message.getChatId(), message.getText());
            replyMessage = handleInput(message);
        }
        return replyMessage;
    }


    private BotApiMethod<?> handleInput(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        if (IGNORE.stream().parallel().anyMatch(inputMsg::contains)) {
            return null;
        }
        if (message.isCommand()) {
            Optional<BasicState> state = EnumUtil.commandToEnum(inputMsg, BasicState.class);
            if (state.isPresent()) {
                switch (state.get()) {
                    case START:
                        if (!cache.existsById(userId))
                            cache.create(userId, chatId);
                        else
                            throw new Error("You should first stop ongoing subscription -> /stop", chatId);
                        break;
                    case STOP:
                        cache.delete(userId, chatId, cache.getLocale(userId));
                        break;
                    case IDLE:
                        return null;
                }
            } else {
                if (appUserDAO.existsCommand(inputMsg))
                    cache.setState(userId, appUserDAO.getStateByCommand(inputMsg).getState());
                else
                    throw new Error("Command not found  ->  " + inputMsg, chatId);
            }
        } else {
            Optional<BasicState> state = EnumUtil.valueOf(cache.getBotState(userId), BasicState.class);
            if (state.isPresent()) {
                if (state.get().equals(BasicState.IDLE)) {
                    return null;
                }
            }
        }

        return cache.existsById(userId) ? stateContext.processInputMessage(cache.getMainState(userId), message) : null;
    }
}
