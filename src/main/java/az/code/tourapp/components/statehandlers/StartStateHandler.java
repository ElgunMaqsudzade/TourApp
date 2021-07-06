package az.code.tourapp.components.statehandlers;

import az.code.tourapp.cache.DataCache;
import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.dtos.BotState;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class StartStateHandler implements InputMessageHandler {

    private DataCache dataCache;
    private MessageService messageService;

    public StartStateHandler(DataCache dataCache, MessageService messageService) {
        this.dataCache = dataCache;
        this.messageService = messageService;
    }

    @Override
    public SendMessage handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        AppUser user = dataCache.getAppUserData(userId);
        BotState botState = user.getBotState();

        SendMessage replyToUser = null;

        if (botState.equals(BotState.START)) {
            replyToUser = messageService.replyMessage(chatId, "Hey");
            dataCache.setAppUserBotState(userId, BotState.SET_LANGUAGE);
        }
        return replyToUser;
    }

    @Override
    public BotState getMainState() {
        return BotState.START;
    }
}
