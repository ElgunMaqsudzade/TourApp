package az.code.tourapp.components;


import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.cache.AppUserCacheImpl;
import az.code.tourapp.components.statehandlers.CallBackHandler;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.enums.BasicCache;
import az.code.tourapp.enums.BasicState;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.jobs.SendMessageJob;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.Reply;
import az.code.tourapp.utils.EnumUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Getter
@Setter
@Slf4j
@Component
public class TelegramWHBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    private final List<String> IGNORE;


    AppUserCache cache;
    BotStateContext stateContext;
    CallBackHandler callBackHandler;
    AppUserDAO appUserDAO;
    BotConfig config;

    public TelegramWHBot(AppUserCache userDataCache, BotStateContext stateContext, CallBackHandler callBackHandler, AppUserDAO appUserDAO, BotConfig config) {
        this.cache = userDataCache;
        this.stateContext = stateContext;
        this.callBackHandler = callBackHandler;
        this.appUserDAO = appUserDAO;
        this.botPath = config.getPath();
        this.botUsername = config.getUsername();
        this.botToken = config.getToken();
        this.IGNORE = config.getIgnore().getHard();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        BotApiMethod<?> replyMessage = null;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    update.getCallbackQuery().getFrom().getFirstName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return callBackHandler.processQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getFirstName(), message.getChatId(), message.getText());
            replyMessage = handleInput(message);
        }
        return replyMessage;
    }

    private BotApiMethod<?> handleInput(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        if (IGNORE.stream().parallel().anyMatch(inputMsg::contains)) {
            return null;
        }
        if (message.isCommand()) {
            Optional<BasicState> state = EnumUtil.commandToEnum(inputMsg, BasicState.class);
            if (state.isPresent()) {
                switch (state.get()) {
                    case START:
                        if (!cache.existsById(userId))
                            cache.createUserData(userId, chatId);
                        else
                            throw new Error("You should first stop ongoing subscription -> /stop" , chatId);
                        break;
                    case STOP:
                        cache.removeUserData(userId, chatId, cache.getLocale(userId));
                        break;
                    case IDLE:
                        return null;
                }
            } else {
                if (appUserDAO.existsCommand(inputMsg))
                    cache.setState(userId, appUserDAO.getStateByCommand(inputMsg).getState());
                else
                    throw new Error("Command not found  ->  " + inputMsg, chatId);
            }
        } else {
            Optional<BasicState> state = EnumUtil.valueOf(cache.getBotState(userId), BasicState.class);
            if (state.isPresent()) {
                if (state.get().equals(BasicState.IDLE)) {
                    return null;
                }
            }
        }

        return cache.existsById(userId) ? stateContext.processInputMessage(cache.getMainState(userId), message) : null;
    }
}
