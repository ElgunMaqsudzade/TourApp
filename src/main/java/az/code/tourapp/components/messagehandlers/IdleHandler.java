package az.code.tourapp.components.messagehandlers;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.interfaces.StateHandler;
import az.code.tourapp.daos.interfaces.AppUserDAO;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class IdleHandler implements StateHandler {
    private final WebhookBotComponent sender;
    private final OfferCache cache;
    private final AppUserDAO userDAO;
    private final SubCacheService cacheService;

    @Override
    public BotApiMethod<?> handleMessage(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        Integer message_id = message.getMessageId();

        System.out.println(usersAnswer);

        return null;
    }

    @Override
    public BotApiMethod<?> handleCallBack(CallbackQuery query) {
        long chatId = query.getMessage().getChatId();
        long userId = query.getFrom().getId();
        Integer message_id = query.getMessage().getMessageId();
        String usersAnswer = query.getData();
        String state = cacheService.getBotState(userId).getState();

        if (state.equals(BasicState.NEXT.toString())) {
            if (usersAnswer.equals("Next")) {
                sender.deleteMessage(chatId, message_id);
                AppUser user = userDAO.findById(userId);
                cache.setLocked(false, user.getUuid());
            }
        }
        return null;
    }

    @Override
    public String getMainState() {
        return BasicState.IDLE.toString();
    }
}
