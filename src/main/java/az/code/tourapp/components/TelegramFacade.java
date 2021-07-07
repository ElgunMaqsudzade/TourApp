package az.code.tourapp.components;

import az.code.tourapp.cache.AppUserCacheImpl;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.dtos.BotState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class TelegramFacade {
    AppUserCacheImpl userDataCache;
    BotStateContext stateContext;
    BotConfig config;
    ActionDAO actionDAO;

    public TelegramFacade(AppUserCacheImpl userDataCache, BotStateContext stateContext, BotConfig config, ActionDAO actionDAO) {
        this.userDataCache = userDataCache;
        this.stateContext = stateContext;
        this.config = config;
        this.actionDAO = actionDAO;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;


        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getFirstName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();


        if (message.isCommand() && inputMsg.equals("/start") && !userDataCache.existsById(userId)) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("STATE", "START");
            userMap.put("MAINSTATE", "START");
            userMap.put("LOCALE", "en-EN");
            userDataCache.saveAppUserData(userId, userMap);
        }


        Map<String, String> appUser = userDataCache.getAppUserData(userId);
        String botState = userDataCache.getAppUserBotState(userId);

//        BotState botState = BasicUtil.getCommandState(inputMsg);
//        appUser.setBotState(botState);

        return stateContext.processInputMessage(botState, message);
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();

        BotApiMethod<?> callBackAnswer = null;

        callBackAnswer = new SendMessage(chatId, "Как тебя зовут ?");
        userDataCache.setAppUserBotState(userId,actionDAO. );


        return callBackAnswer;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

}
