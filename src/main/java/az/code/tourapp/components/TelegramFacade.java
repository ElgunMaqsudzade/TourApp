package az.code.tourapp.components;

import az.code.tourapp.components.statehandlers.CallBackHandler;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.services.AppUserCacheService;
import az.code.tourapp.utils.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@Slf4j
@Component
public class TelegramFacade {
    private final AppUserCacheService cache;
    private final BotStateContext stateContext;
    private final CallBackHandler callBackHandler;
    private final SchedulerExecutor sch;

    private final String IGNORE;

    public TelegramFacade(AppUserCacheService cache, BotStateContext stateContext, CallBackHandler callBackHandler, SchedulerExecutor sch, BotConfig config) {
        this.cache = cache;
        this.stateContext = stateContext;
        this.callBackHandler = callBackHandler;
        this.sch = sch;
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
        if (IGNORE.equals(inputMsg)) {
            return null;
        }
        if (message.isCommand()) {
            handleCommand(chatId, userId, inputMsg);
        }
        Optional<BasicState> state = EnumUtil.valueOf(cache.getBotState(userId), BasicState.class);
        if (state.isPresent()) {
            switch (state.get()) {
                case IDLE:
                    return null;
                case SUBSCRIPTION_END:
                    sch.runSubscribeJob(userId);
            }
        }

        return cache.existsById(userId) ? stateContext.processInputMessage(cache.getMainState(userId), message) : null;
    }

    private void handleCommand(Long chatId, Long userId, String inputMsg) {
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
            }
        }
//        if (appUserDAO.existsCommand(inputMsg))
//            cache.setState(userId, appUserDAO.getStateByCommand(inputMsg).getState());
//        else
//            throw new Error("Command not found  ->  " + inputMsg, chatId);
    }
}
