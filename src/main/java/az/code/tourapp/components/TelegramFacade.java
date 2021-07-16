package az.code.tourapp.components;

import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.services.SubCacheService;
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
    private final MessageSender sender;

    private final String IGNORE;

    public TelegramFacade(MessageSender sender, SubCacheService cache, BotStateContext stateContext, BotConfig config) {
        this.cache = cache;
        this.sender = sender;
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
                switch (state.get()) {
                    case START:
                        if (!cache.existsById(userId))
                            cache.create(userId, chatId);
                        else
                            throw new Error("You should first stop ongoing subscription -> /stop", chatId);
                        break;
                    case STOP:
                        if (cache.existsById(userId)) {
                            cache.delete(userId, chatId);
                        }
                        return null;
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
