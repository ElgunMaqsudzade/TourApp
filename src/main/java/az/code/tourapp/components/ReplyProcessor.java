package az.code.tourapp.components;


import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.dtos.KeyboardDTO;
import az.code.tourapp.enums.BasicCache;
import az.code.tourapp.enums.BasicState;
import az.code.tourapp.enums.InputType;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.models.*;
import az.code.tourapp.services.AppUserCacheService;
import az.code.tourapp.utils.CalendarUtil;
import az.code.tourapp.utils.KeyboardContext;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReplyProcessor {
    ReplyDAO replyDAO;
    ActionDAO actionDAO;
    AppUserDAO appUserDAO;
    KeyboardContext context;
    AppUserCacheService cache;
    @Autowired
    SendMessageComponent sender;

    private final List<String> IGNORE;

    public ReplyProcessor(ReplyDAO replyDAO, ActionDAO actionDAO, AppUserDAO appUserDAO, KeyboardContext context, AppUserCacheService appUserCache, BotConfig config) {
        this.replyDAO = replyDAO;
        this.actionDAO = actionDAO;
        this.appUserDAO = appUserDAO;
        this.context = context;
        this.cache = appUserCache;
        this.IGNORE = config.getIgnore().getSave();
    }

    public BotApiMethod<?> processNextAction(Long userId, Long chatId, Integer message_id, String usersAnswer) {
        String locale = cache.getLocale(userId);
        while (true) {
            String botState = cache.getBotState(userId);
            Optional<Action> opAction = actionDAO.getAction(botState, usersAnswer, locale);
            Reply errorReply = replyDAO.getReply(BasicState.ERROR.toString(), locale);
            if (opAction.isEmpty()) throw new Error(errorReply.getMessage(), chatId);
            Action action = opAction.get();
            if (action.getInputType().equals(InputType.VOID)) {
                Reply reply = replyDAO.getReply(botState, locale);
                sender.sendMessage(new SendMessage(String.valueOf(chatId), reply.getMessage()));
                Optional<InputType> inputType = actionDAO.getNextActionType(action.getNextState().getState());
                if (inputType.isPresent()) {
                    if (inputType.get().equals(InputType.VOID))
                        cache.setState(userId, action.getNextState().getState());
                    else break;
                } else return null;
            } else break;
        }
        return processAnswer(userId, chatId, message_id, usersAnswer);
    }

    public BotApiMethod<?> processAnswer(Long userId, Long chatId, Integer message_id, String usersAnswer) {
        if (IGNORE.stream().parallel().anyMatch(usersAnswer::contains)) {
            return processFinalAction(userId, chatId, message_id, usersAnswer);
        }
        String locale = cache.getLocale(userId);
        String botState = cache.getBotState(userId);
        Reply errorReply = replyDAO.getReply(BasicState.ERROR.toString(), locale);

        Optional<Action> action = actionDAO.getAction(botState, usersAnswer, locale);
        if (action.isPresent()) {
            if (!cache.saveData(userId, botState, usersAnswer))
                throw new Error(errorReply.getMessage(), chatId);
            cache.setState(userId, action.get().getNextState().getState());
        } else
            throw new Error(errorReply.getMessage(), chatId);

        return processFinalAction(userId, chatId, message_id, usersAnswer);
    }

    public BotApiMethod<?> processFinalAction(Long userId, Long chatId, Integer message_id, String usersAnswer) {
        String botState = cache.getBotState(userId);
        String locale = cache.getLocale(userId);


        Reply reply = replyDAO.getReply(botState, locale);
        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId.toString());
        replyToUser.setText(reply.getMessage());

        List<ActionInput> actionList = actionDAO.getActionInputList(botState, locale);


        List<KeyboardDTO> buttons = actionList.stream()
                .map(i -> KeyboardDTO.builder().inputType(i.getAction().getInputType()).text(i.getText()).callbackData(i.getText()).build())
                .collect(Collectors.toList());

        if (botState.equals(BasicCache.LOCALE.toString()))
            buttons = appUserDAO.getAllLanguages().stream().map(i -> KeyboardDTO.builder()
                    .callbackData(i.getLang()).text(i.getLang())
                    .inputType(InputType.BUTTON).build())
                    .collect(Collectors.toList());

        ReplyKeyboard replyKeyboard = context.generateKeyboard(buttons);

        replyToUser.setReplyMarkup(replyKeyboard);

        String regex = appUserDAO.getState(botState).getRegex();

        if (regex != null && regex.equals(InputType.DATE.toString())) {
            CalendarUtil calendarUtil = new CalendarUtil();
            InlineKeyboardMarkup keyboardMarkup = null;
            if (IGNORE.stream().anyMatch(usersAnswer::contains)) {
                if (usersAnswer.contains(IGNORE.get(0)))
                    keyboardMarkup = calendarUtil.generateKeyboard(LocalDate.parse(usersAnswer.substring(2)).plusDays(30), IGNORE.get(0));
                else if (usersAnswer.contains(IGNORE.get(1))) {
                    keyboardMarkup = calendarUtil.generateKeyboard(LocalDate.parse(usersAnswer.substring(2)).minusDays(30), IGNORE.get(0));
                }
                sender.editMessage(chatId, message_id, reply.getMessage(), keyboardMarkup);
                return null;
            } else {
                replyToUser.setReplyMarkup(calendarUtil.generateKeyboard(LocalDate.now(), IGNORE.get(0)));
            }
        }

        return replyToUser;
    }

}
