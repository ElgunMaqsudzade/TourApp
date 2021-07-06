package az.code.tourapp.components;

import az.code.tourapp.cache.UserDataCache;
import az.code.tourapp.dtos.BotState;
import az.code.tourapp.models.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext stateContext;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext stateContext) {
        this.userDataCache = userDataCache;
        this.stateContext = stateContext;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getFirstName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();

        if (message.isCommand() && inputMsg.equals("/start")) {
            userDataCache.saveAppUserData(userId, AppUser
                    .builder()
                    .id(userId)
                    .mainState(BotState.START)
                    .botState(BotState.START)
                    .build());
        }

        AppUser appUser = userDataCache.getAppUserData(userId);

//        BotState botState = BasicUtil.getCommandState(inputMsg);
//        appUser.setBotState(botState);

        return stateContext.processInputMessage(appUser.getMainState(), message);
    }


}
