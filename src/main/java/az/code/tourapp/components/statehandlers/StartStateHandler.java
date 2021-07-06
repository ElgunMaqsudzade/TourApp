package az.code.tourapp.components.statehandlers;

import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.MessageComponent;
import az.code.tourapp.components.interfaces.InputMessageHandler;
import az.code.tourapp.dtos.AppUserDTO;
import az.code.tourapp.dtos.BotState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class StartStateHandler implements InputMessageHandler {
    AppUserCache dataCache;
    MessageComponent messageService;

    public StartStateHandler(AppUserCache dataCache, MessageComponent messageComponent) {
        this.dataCache = dataCache;
        this.messageService = messageComponent;
    }

    @Override
    public SendMessage handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        AppUserDTO user = dataCache.getAppUserData(userId);
        BotState botState = user.getBotState();

        SendMessage replyToUser = messageService.staticReplyMessage(chatId,
                "reply." + user.getBotState(),
                user.getLang());

        return replyToUser;
    }

    @Override
    public BotState getMainState() {
        return BotState.START;
    }
}
