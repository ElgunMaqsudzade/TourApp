package az.code.tourapp.components.messagehandlers;

import az.code.tourapp.components.MessageSender;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.interfaces.StateHandler;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Reply;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.services.SubCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class SubscriptionHandler implements StateHandler {
    private final ReplyProcessor replyUtil;
    private final SubCacheService cache;
    private final ReplyDAO replyDAO;
    private final ActionDAO actionDAO;
    private final MessageSender sender;

    private final List<String> IGNORE;

    public SubscriptionHandler(ReplyProcessor replyUtil, SubCacheService cache, ReplyDAO replyDAO, ActionDAO actionDAO, MessageSender sender, BotConfig config) {
        this.replyUtil = replyUtil;
        this.cache = cache;
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.sender = sender;
        this.IGNORE = config.getIgnore().getSave();
    }


    @Override
    public BotApiMethod<?> handleMessage(Message message) {

        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Integer message_id = message.getMessageId();

        return replyUtil.processNextAction(userId, chatId, message_id, usersAnswer);
    }

    @Override
    public BotApiMethod<?> handleCallBack(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        long userId = query.getFrom().getId();
        Integer message_id = query.getMessage().getMessageId();
        String usersAnswer = query.getData();

        if (IGNORE.stream().parallel().anyMatch(usersAnswer::contains)) {
            return replyUtil.processFinalAction(userId, chatId, message_id, usersAnswer);
        }
        String locale = cache.getLocale(userId);
        String botState = cache.getBotState(userId).getState();
        Reply errorReply = replyDAO.getReply(BasicState.ERROR.toString(), locale);

        Optional<Action> action = actionDAO.getAction(botState, usersAnswer, locale);
        if (action.isPresent()) {
            if (action.get().getCurrentState().isSavable()) {
                if (!cache.saveData(userId, botState, usersAnswer))
                    throw new Error(errorReply.getMessage(), chatId);
            }
            sender.editMessage(chatId, message_id, usersAnswer, null);
            cache.setState(userId, action.get().getNextState().getState());
        } else
            throw new Error(errorReply.getMessage(), chatId);


        return replyUtil.processFinalAction(userId, chatId, message_id, usersAnswer);
    }

    @Override
    public String getMainState() {
        return BasicState.START.toString();
    }
}
