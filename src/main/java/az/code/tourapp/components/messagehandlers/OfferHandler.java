package az.code.tourapp.components.messagehandlers;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.interfaces.StateHandler;
import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.UserDataDTO;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.components.WebhookBotComponent;
import az.code.tourapp.services.interfaces.SubCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferHandler implements StateHandler {
    private final WebhookBotComponent sender;
    private final OfferCache cache;
    private final AppUserDAO userDAO;
    private final SubCacheService cacheService;
    private final ActionDAO actionDAO;
    private final ReplyProcessor replyProcessor;

    @Override
    public BotApiMethod<?> handleMessage(Message message) {

        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Integer message_id = message.getMessageId();

        if (message.isReply()) {
            UserDataDTO userDataDTO = cacheService.findById(userId);
            Map<String, String> offer = userDataDTO.getSubscription();
            offer.put("firstName", message.getFrom().getFirstName());
            offer.put("lastName", message.getFrom().getLastName());
            offer.put("messageText", message.getReplyToMessage().getCaption());
            offer.put("messageId", String.valueOf(message.getReplyToMessage().getMessageId()));
            cacheService.update(userId, userDataDTO);
            cacheService.setState(userId, BasicState.PHONE_CHOICE.toString());
            cacheService.setMainState(userId, BasicState.CLIENT);
            replyProcessor.processFinalAction(userId, chatId, message_id, usersAnswer);
        }

        return replyProcessor.sendNextAction(userId, chatId);
    }

    @Override
    public BotApiMethod<?> handleCallBack(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        long userId = query.getFrom().getId();
        Integer message_id = query.getMessage().getMessageId();
        String usersAnswer = query.getData();
        String locale = cacheService.getLocale(userId);
        Optional<Action> action = actionDAO.getAction(BasicState.NEXT.toString(), usersAnswer, locale);

        if (action.isPresent()) {
            sender.deleteMessage(chatId, message_id);
            AppUser user = userDAO.findById(userId);
            cache.setLocked(false, user.getUuid());
            cacheService.setState(userId, BasicState.OFFER.toString());
            return null;
        }

        return replyProcessor.processNextAction(userId, chatId, message_id, usersAnswer);
    }

    @Override
    public String getMainState() {
        return BasicState.OFFER.toString();
    }
}
