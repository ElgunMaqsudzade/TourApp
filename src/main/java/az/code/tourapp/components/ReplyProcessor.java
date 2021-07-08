package az.code.tourapp.components;


import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.dtos.BasicState;
import az.code.tourapp.dtos.DataType;
import az.code.tourapp.dtos.InputType;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.ActionInput;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Reply;
import az.code.tourapp.utils.CalendarUtil;
import az.code.tourapp.utils.KeyboardContext;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReplyProcessor {
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    AppUserDAO appUserDAO;
    KeyboardContext context;
    AppUserCache appUserCache;

    public ReplyProcessor(ReplyDAO replyDAO, ActionDAO actionDAO, AppUserDAO appUserDAO, KeyboardContext context, AppUserCache appUserCache) {
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.appUserDAO = appUserDAO;
        this.context = context;
        this.appUserCache = appUserCache;
    }

    public BotApiMethod<?> processReply(Long userId, Long chatId, String usersAnswer) {
        Map<String, String> user = appUserCache.getAppUserData(userId);
        if (user == null) throw new NotFound("User couldn't found");
        if (usersAnswer.matches("[^<>]*")) {
            String botState = appUserCache.getBotState(userId);
            user.put(botState, usersAnswer);
            appUserCache.saveAppUserData(userId, user);
            Action chosenAction = actionDAO.getChosenAction(botState, usersAnswer);
            BotState nextState = chosenAction.getNextState();
            appUserCache.setAppUserBotState(userId, nextState);
        }
        return processMessage(userId, chatId, usersAnswer);
    }

    private BotApiMethod<?> processMessage(Long userId, Long chatId, String usersAnswer) {
        Map<String, String> user = appUserCache.getAppUserData(userId);

        String botState = appUserCache.getBotState(userId);
        String locale = user.get(BasicState.LOCALE.toString());

        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId.toString());


        List<ActionInput> actionList = actionDAO.getActionInputList(botState, locale);

        Map<String, InputType> buttons = actionList
                .stream()
                .collect(Collectors.toMap(ActionInput::getText, i -> i.getAction().getInputType()));

        ReplyKeyboard replyKeyboard = context.generateKeyboard(buttons);

        Reply reply = replyDAO.getReply(botState, locale);

        replyToUser.setText(reply.getMessage());

        switch (appUserDAO.getState(botState).getDataType()) {
            case DATE:
                CalendarUtil calendarUtil = new CalendarUtil();
                if (usersAnswer.startsWith(">"))
                    replyToUser.setReplyMarkup(calendarUtil.generateKeyboard(LocalDate.parse(usersAnswer.substring(1)).plusDays(30)));
                if (usersAnswer.startsWith("<"))
                    replyToUser.setReplyMarkup(calendarUtil.generateKeyboard(LocalDate.parse(usersAnswer.substring(1)).minusDays(30)));
                if (usersAnswer.matches("[^<>]*"))
                    replyToUser.setReplyMarkup(calendarUtil.generateKeyboard(LocalDate.now()));
                break;
            default:
                replyToUser.setReplyMarkup(replyKeyboard);
                break;
        }


        return replyToUser;
    }
}
