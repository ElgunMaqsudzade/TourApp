package az.code.tourapp.components;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.SubCacheService;
import az.code.tourapp.utils.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@Slf4j
@Component
public class TelegramFacade {
    private final SubCacheService cache;
    private final BotStateContext stateContext;
    private final AppUserDAO appUserDAO;
    private final OfferCache offerCache;

    private final String IGNORE;

    public TelegramFacade(OfferCache offerCache, AppUserDAO appUserDAO, SubCacheService cache, BotStateContext stateContext, BotConfig config) {
        this.appUserDAO = appUserDAO;
        this.offerCache = offerCache;
        this.cache = cache;
        this.stateContext = stateContext;
        this.IGNORE = config.getIgnore().getHard();
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        BotApiMethod<?> replyMessage = null;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    update.getCallbackQuery().getFrom().getFirstName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return handleCallBack(callbackQuery);
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
        if (IGNORE.equals(inputMsg)) {
            return null;
        }
        if (message.isCommand()) {
            Optional<BasicState> state = EnumUtil.commandToEnum(inputMsg, BasicState.class);
            if (state.isPresent()) {
                if (state.get().equals(BasicState.START)) {
                    if (!cache.existsById(userId))
                        cache.create(userId, chatId);
                    else
                        throw new Error("You should first stop ongoing subscription -> /stop", chatId);
                }
                if (cache.existsById(userId)) {
                    cache.setState(userId, state.get().toString());
                    switch (state.get()) {
                        case STOP:
                            AppUser appUser = appUserDAO.findById(userId);
                            offerCache.delete(appUser.getUuid());
                            cache.delete(userId, chatId);
                            return null;
                    }
                }else{
                    throw new Error("You dont have any ongoing subscription. Please write -> /start for starting.", chatId);
                }
            }
        }
        return cache.existsById(userId) ? stateContext.processInputMessage(cache.getMainState(userId), message) : null;
    }

    private BotApiMethod<?> handleCallBack(CallbackQuery query) {
        long userId = query.getFrom().getId();
        String inputMsg = query.getData();
        if (IGNORE.equals(inputMsg)) {
            return null;
        }

        return cache.existsById(userId) ? stateContext.processCallBack(cache.getMainState(userId), query) : null;
    }
}
