package az.code.tourapp.components;


import az.code.tourapp.MessageComponent;
import az.code.tourapp.cache.AppUserCacheImpl;
import az.code.tourapp.components.statehandlers.CallBackHandler;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class TelegramFacade {
    AppUserCacheImpl userDataCache;
    BotStateContext stateContext;
    CallBackHandler callBackHandler;
    MessageComponent messageComponent;
    AppUserDAO appUserDAO;


    public TelegramFacade(AppUserCacheImpl userDataCache, BotStateContext stateContext, CallBackHandler callBackHandler, MessageComponent messageComponent, AppUserDAO appUserDAO) {
        this.userDataCache = userDataCache;
        this.stateContext = stateContext;
        this.callBackHandler = callBackHandler;
        this.messageComponent = messageComponent;
        this.appUserDAO = appUserDAO;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        BotApiMethod<?> replyMessage = null;


        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getFirstName(),
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


        if (inputMsg.equals("/start") && !userDataCache.existsById(userId)) {
            Map<String, String> userMap = new HashMap<>();
            String state = appUserDAO.getStateByCommand(inputMsg).getState();
            Optional<AppUser> appUser = appUserDAO.findById(userId);
            userMap.put("STATE", state);
            userMap.put("MAINSTATE", state);

            if (appUser.isPresent()) {
                userMap.put("LOCALE", appUser.get().getLocale().getLang());
                userDataCache.saveAppUserData(userId, userMap);
            } else {
                userMap.put("LOCALE", "ENG");
                userDataCache.saveAppUserData(userId, userMap);
            }
        }
        if (inputMsg.equals("/stop") && userDataCache.existsById(userId)) {
            Map<String, String> userMap = userDataCache.getAppUserData(userId);
            String state = appUserDAO.getStateByCommand(inputMsg).getState();
            userMap.put("STATE", state);
            userDataCache.saveAppUserData(userId, userMap);
        }

        Map<String, String> appUser = userDataCache.getAppUserData(userId);
        String mainState = userDataCache.getMainState(userId);
        String state = userDataCache.getBotState(userId);

        if (message.isCommand() && !inputMsg.equals("/start")) {
            if (appUserDAO.existsCommand(inputMsg)) {
                appUser.put("STATE", appUserDAO.getStateByCommand(inputMsg).getState());

            } else {
                messageComponent.sendMessage(message.getChatId(), "");
                throw new Error("Command not found  ->  " + message.getText(), message.getChatId());
            }
        }
        return userDataCache.existsById(userId) ? stateContext.processInputMessage(mainState, message) : null;
    }
}
