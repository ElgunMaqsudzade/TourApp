package az.code.tourapp.components.statehandlers;

import az.code.tourapp.MessageComponent;
import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.dtos.BasicState;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Reply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

@Slf4j
@Component
public class StartStateHandler implements InputMessageHandler {
    AppUserCache dataCache;
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    ReplyProcessor replyUtil;
    MessageComponent messageComponent;

    public StartStateHandler(AppUserCache dataCache, ReplyDAO replyDAO, ActionDAO actionDAO, ReplyProcessor replyUtil, MessageComponent messageComponent) {
        this.dataCache = dataCache;
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.replyUtil = replyUtil;
        this.messageComponent = messageComponent;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Map<String, String> user = dataCache.getAppUserData(userId);
        String locale = user.get(BasicState.LOCALE.toString());

        String botState = dataCache.getBotState(userId);
        Action chosenAction = actionDAO.getChosenAction(botState, usersAnswer);
        if(botState.equals("STOP")){
            dataCache.removeAppUser(userId);
        }
        if (chosenAction.getInputType() == null) {
            Reply reply = replyDAO.getReply(botState, locale);
            messageComponent.sendMessage(chatId, reply.getMessage());
        }
        return replyUtil.processReply(userId, chatId, usersAnswer);
    }

    @Override
    public String getMainState() {
        return BasicState.START.toString();
    }
}
