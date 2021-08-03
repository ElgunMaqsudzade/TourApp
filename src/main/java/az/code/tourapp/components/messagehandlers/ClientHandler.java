package az.code.tourapp.components.messagehandlers;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.WebhookBotComponent;
import az.code.tourapp.components.interfaces.StateHandler;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.dtos.UserDataDTO;
import az.code.tourapp.exceptions.ValidationException;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Error;
import az.code.tourapp.models.Reply;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.services.interfaces.SubCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


@Slf4j
@Component
public class ClientHandler implements StateHandler {
    private final ReplyProcessor replyProcessor;
    private final SubCacheService cache;
    private final DictionaryCache dCache;
    private final ActionDAO actionDAO;
    private final WebhookBotComponent sender;

    private final List<String> IGNORE;

    public ClientHandler(ReplyProcessor replyProcessor,
                         SubCacheService cache,
                         DictionaryCache dCache,
                         ActionDAO actionDAO,
                         WebhookBotComponent sender,
                         BotConfig config) {
        this.replyProcessor = replyProcessor;
        this.cache = cache;
        this.dCache = dCache;
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

        if (message.hasContact()) {
            Contact contact = message.getContact();
            UserDataDTO userDataDTO = cache.findById(userId);
            Map<String, String> offer = userDataDTO.getSubscription();
            offer.put("firstName", contact.getFirstName());
            offer.put("lastName", contact.getLastName());
            offer.put("phoneNumber", contact.getPhoneNumber());
            cache.update(userId, userDataDTO);
            usersAnswer = "";
        } else {
            return null;
        }

        return replyProcessor.processNextAction(userId, chatId, message_id, usersAnswer);
    }

    @Override
    public BotApiMethod<?> handleCallBack(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        long userId = query.getFrom().getId();
        Integer message_id = query.getMessage().getMessageId();
        String usersAnswer = query.getData();

        if (IGNORE.stream().parallel().anyMatch(usersAnswer::contains)) {
            return replyProcessor.processFinalAction(userId, chatId, message_id, usersAnswer);
        }
        String locale = cache.getLocale(userId);
        String botState = cache.getBotState(userId).getState();
        Error errorReply = dCache.getError(botState, locale);



        Optional<Action> action = actionDAO.getAction(botState, usersAnswer, locale);
        if (action.isPresent()) {
            if (action.get().getCurrentState().isSavable()) {
                if (!cache.saveData(userId, botState, usersAnswer))
                    throw new ValidationException(errorReply.getErrorMessage(), chatId);
            }
            sender.sendEditedMessage(chatId, message_id, usersAnswer, null);
            cache.setState(userId, action.get().getNextState().getState());
        } else
            throw new ValidationException(errorReply.getErrorMessage(), chatId);


        return replyProcessor.processFinalAction(userId, chatId, message_id, usersAnswer);
    }

    @Override
    public String getMainState() {
        return BasicState.CLIENT.toString();
    }
}
