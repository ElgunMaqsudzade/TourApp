package az.code.tourapp.components.statehandlers;

import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.MessageComponent;
import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.dtos.BotState;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;
import az.code.tourapp.repos.ReplyRepo;
import az.code.tourapp.utils.InlineKeyboardUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StartStateHandler implements InputMessageHandler {
    AppUserCache dataCache;
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    MessageComponent messageService;

    public StartStateHandler(AppUserCache dataCache, ReplyDAO replyDAO, ActionDAO actionDAO, MessageComponent messageService) {
        this.dataCache = dataCache;
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.messageService = messageService;
    }

    @Override
    public SendMessage handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        Map<String, String> user = dataCache.getAppUserData(userId);
        String botState = dataCache.getAppUserBotState(userId);
        String locale = dataCache.getAppUserData(userId).get("LOCALE");
        Reply reply = replyDAO.getReply(botState, locale);


        Action currentAction = actionDAO.getAction(botState, usersAnswer);
        List<Action> nextActions = actionDAO.getNextActionList(currentAction);
        List<String> buttonContents = nextActions.stream().map(Action::getStaticText).collect(Collectors.toList());
        SendMessage replyToUser = messageService.replyMessage(chatId, reply.getMessage());
        replyToUser.setReplyMarkup(InlineKeyboardUtil.getInlineButtons(buttonContents));
        dataCache.setAppUserBotState(userId, currentAction.getNextState());

        return replyToUser;
    }

    @Override
    public String getMainState() {
        return "START";
    }
}
