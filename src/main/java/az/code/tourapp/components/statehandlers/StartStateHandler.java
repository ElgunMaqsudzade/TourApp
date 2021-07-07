package az.code.tourapp.components.statehandlers;

import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.ProcessReplyComponent;
import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Reply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class StartStateHandler implements InputMessageHandler {
    AppUserCache dataCache;
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    ProcessReplyComponent replyUtil;

    public StartStateHandler(AppUserCache dataCache, ReplyDAO replyDAO, ActionDAO actionDAO, ProcessReplyComponent replyUtil) {
        this.dataCache = dataCache;
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.replyUtil = replyUtil;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        return replyUtil.processReply(userId, chatId, usersAnswer);
    }

    @Override
    public String getMainState() {
        return "START";
    }
}
