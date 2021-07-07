package az.code.tourapp.components;


import az.code.tourapp.MessageComponent;
import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Reply;
import az.code.tourapp.utils.InlineKeyboardUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProcessReplyComponent {
    AppUserCache dataCache;
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    MessageComponent messageComponent;

    public ProcessReplyComponent(AppUserCache dataCache, ReplyDAO replyDAO, ActionDAO actionDAO, MessageComponent messageComponent) {
        this.dataCache = dataCache;
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.messageComponent = messageComponent;
    }

    public BotApiMethod<?> processReply(Long userId, Long chatId, String usersAnswer) {

        Map<String, String> user = dataCache.getAppUserData(userId);
        String botState = dataCache.getAppUserBotState(userId);
        String locale = dataCache.getAppUserData(userId).get("LOCALE");


        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId.toString());
        Action chosenAction = actionDAO.getChosenAction(botState, usersAnswer);


        if (chosenAction.getInputType() == null) {
            Reply reply = replyDAO.getReply(chosenAction.getCurrentState().getState(), locale);
            messageComponent.sendMessage(chatId, reply.getMessage());
        } else {
            user.put(botState, usersAnswer);
            dataCache.saveAppUserData(userId, user);
        }
        if (chosenAction.getNextState() != null) {
            dataCache.setAppUserBotState(userId, chosenAction.getNextState());

            String newBotState = dataCache.getAppUserBotState(userId);

            List<Action> actionList = actionDAO.getActionList(newBotState);

            Reply reply = replyDAO.getReply(newBotState, locale);
            if (!actionList.isEmpty()) {
                List<String> buttonContents = actionList.stream()
                        .map(Action::getStaticText).collect(Collectors.toList());

                replyToUser.setText(reply.getMessage());
                replyToUser.setReplyMarkup(InlineKeyboardUtil.getInlineButtons(buttonContents));
            }

        }
        return replyToUser;
    }
}
