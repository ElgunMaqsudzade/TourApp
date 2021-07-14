package az.code.tourapp.components.statehandlers;

import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.models.enums.BasicState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;


@Slf4j
@Component
public class StartStateHandler implements InputMessageHandler {
    AppUserCache dataCache;
    ReplyProcessor replyUtil;

    public StartStateHandler(AppUserCache dataCache, ReplyProcessor replyUtil) {
        this.dataCache = dataCache;
        this.replyUtil = replyUtil;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Integer message_id = message.getMessageId();

        return replyUtil.processNextAction(userId, chatId, message_id, usersAnswer);
    }

    @Override
    public String getMainState() {
        return BasicState.START.toString();
    }
}
