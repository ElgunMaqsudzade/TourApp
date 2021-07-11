package az.code.tourapp.components.statehandlers;


import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.SendMessageComponent;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.enums.BasicState;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Reply;
import az.code.tourapp.services.AppUserCacheService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;


@Component
public class CallBackHandler {
    ReplyProcessor replyUtil;
    AppUserCacheService cache;
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    SendMessageComponent sender;

    private final List<String> IGNORE;

    public CallBackHandler(ReplyProcessor replyUtil, AppUserCacheService cache, ReplyDAO replyDAO, ActionDAO actionDAO, SendMessageComponent sender,BotConfig config) {
        this.replyUtil = replyUtil;
        this.cache = cache;
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.sender = sender;
        this.IGNORE = config.getIgnore().getSave();
    }

    public BotApiMethod<?> processQuery(CallbackQuery buttonQuery) {
        long chatId = buttonQuery.getMessage().getChatId();
        long userId = buttonQuery.getFrom().getId();
        Integer message_id = buttonQuery.getMessage().getMessageId();
        String usersAnswer = buttonQuery.getData();

        if (IGNORE.stream().parallel().anyMatch(usersAnswer::contains)) {
            return replyUtil.processFinalAction(userId, chatId, message_id, usersAnswer);
        }
        String locale = cache.getLocale(userId);
        String botState = cache.getBotState(userId);
        Reply errorReply = replyDAO.getReply(BasicState.ERROR.toString(), locale);

        Optional<Action> action = actionDAO.getAction(botState, usersAnswer, locale);
        if (action.isPresent()) {
            if (!cache.saveData(userId, botState, usersAnswer))
                throw new Error(errorReply.getMessage(), chatId);
            sender.editMessage(chatId, message_id, usersAnswer, null);
            cache.setState(userId, action.get().getNextState().getState());
        } else
            throw new Error(errorReply.getMessage(), chatId);


        return replyUtil.processFinalAction(userId, chatId, message_id, usersAnswer);
    }

    private AnswerCallbackQuery sendQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

}
