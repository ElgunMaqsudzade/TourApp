package az.code.tourapp.components;


import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.dtos.KeyboardDTO;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.models.enums.InputType;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.models.*;
import az.code.tourapp.services.SubCacheService;
import az.code.tourapp.utils.KeyboardContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReplyProcessor {
    private final ActionDAO actionDAO;
    private final KeyboardContext context;
    private final SubCacheService cache;
    private final DictionaryCache dCache;
    private final MessageSender sender;
    private final SchedulerExecutor sch;

    private final List<String> IGNORE;

    public ReplyProcessor(SchedulerExecutor sch, ActionDAO actionDAO, KeyboardContext context, SubCacheService cache, DictionaryCache dCache, MessageSender sender, BotConfig config) {
        this.actionDAO = actionDAO;
        this.sch = sch;
        this.context = context;
        this.cache = cache;
        this.dCache = dCache;
        this.sender = sender;
        this.IGNORE = config.getIgnore().getSave();
    }


    public BotApiMethod<?> processNextAction(Long userId, Long chatId, Integer message_id, String usersAnswer) {
        String locale = cache.getLocale(userId);
        while (true) {
            String botState = cache.getBotState(userId).getState();
            Optional<Action> opAction = actionDAO.getAction(botState, usersAnswer, locale);
            Reply errorReply = dCache.getReply(BasicState.ERROR.toString(), locale);
            if (opAction.isEmpty()) throw new Error(errorReply.getMessage(), chatId);
            Action action = opAction.get();
            if (action.getInputType().equals(InputType.VOID)) {
                Reply reply = dCache.getReply(botState, locale);
                sender.sendMessage(new SendMessage(String.valueOf(chatId), reply.getMessage()));
                Optional<InputType> inputType = actionDAO.getInputType(action.getNextState().getState());
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
        String botState = cache.getBotState(userId).getState();
        Reply errorReply = dCache.getReply(BasicState.ERROR.toString(), locale);
        Optional<Action> action = actionDAO.getAction(botState, usersAnswer, locale);

        if (action.isPresent()) {
            if(action.get().getCurrentState().isSavable()){
                if (!cache.saveData(userId, botState, usersAnswer))
                    throw new Error(errorReply.getMessage(), chatId);
            }
            cache.setState(userId, action.get().getNextState().getState());
        } else
            throw new Error(errorReply.getMessage(), chatId);

        return processFinalAction(userId, chatId, message_id, usersAnswer);
    }

    public BotApiMethod<?> processFinalAction(Long userId, Long chatId, Integer message_id, String usersAnswer) {
        String botState = cache.getBotState(userId).getState();
        String locale = cache.getLocale(userId);


        Reply reply = dCache.getReply(botState, locale);
        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId.toString());
        replyToUser.setText(reply.getMessage());

        List<ActionInput> actionList = actionDAO.getActionInputList(botState, locale);
        Optional<InputType> inputType = actionDAO.getInputType(botState);

        List<KeyboardDTO> buttons = null;
        if (inputType.get().equals(InputType.DATE)) {
            buttons = Collections.singletonList(KeyboardDTO.builder().text(usersAnswer).inputType(InputType.DATE).build());
        } else {
            buttons = actionList.stream()
                    .map(i -> KeyboardDTO.builder().inputType(i.getAction().getInputType()).text(i.getText()).callbackData(i.getText()).build())
                    .collect(Collectors.toList());
        }

        ReplyKeyboard replyKeyboard = context.generateKeyboard(buttons);

        replyToUser.setReplyMarkup(replyKeyboard);

        if (IGNORE.stream().anyMatch(usersAnswer::contains)) {
            sender.editMessage(chatId, message_id, reply.getMessage(), (InlineKeyboardMarkup) replyKeyboard);
            return null;
        }

        if(botState.equals(BasicState.SUBSCRIPTION_END.toString())){
            sch.runSubscribeJob(userId);
        }

        return replyToUser;
    }

}
