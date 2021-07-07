package az.code.tourapp.components;


import az.code.tourapp.TelegramWHBot;
import az.code.tourapp.cache.AppUserCacheImpl;
import az.code.tourapp.components.statehandlers.CallBackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TelegramFacade {
    AppUserCacheImpl userDataCache;
    BotStateContext stateContext;
    CallBackHandler callBackHandler;

    public TelegramFacade(AppUserCacheImpl userDataCache, BotStateContext stateContext, CallBackHandler callBackHandler) {
        this.userDataCache = userDataCache;
        this.stateContext = stateContext;
        this.callBackHandler = callBackHandler;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        BotApiMethod<?> replyMessage = null;


        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return callBackHandler.processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getFirstName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private BotApiMethod<?> handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();


        if (message.isCommand() && inputMsg.equals("/start") && !userDataCache.existsById(userId)) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("STATE", "START");
            userMap.put("MAINSTATE", "START");
            userMap.put("LOCALE", "ENG");
            userDataCache.saveAppUserData(userId, userMap);
        }


        Map<String, String> appUser = userDataCache.getAppUserData(userId);
        String mainState = appUser.get("MAINSTATE");

//        BotState botState = BasicUtil.getCommandState(inputMsg);
//        appUser.setBotState(botState);

        return stateContext.processInputMessage(mainState, message);
    }
}
