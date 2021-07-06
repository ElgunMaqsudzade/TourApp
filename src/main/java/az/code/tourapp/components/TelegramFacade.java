package az.code.tourapp.components;

import az.code.tourapp.cache.AppUserCacheImpl;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.AppUserDTO;
import az.code.tourapp.dtos.BotState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component
@Slf4j
public class TelegramFacade {
    AppUserCacheImpl userDataCache;
    BotStateContext stateContext;
    BotConfig config;

    public TelegramFacade(AppUserCacheImpl userDataCache, BotStateContext stateContext, BotConfig config) {
        this.userDataCache = userDataCache;
        this.stateContext = stateContext;
        this.config = config;
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

        if (message.isCommand() && inputMsg.equals("/start") && userDataCache.existsById(userId)) {
            userDataCache.saveAppUserData(userId, AppUserDTO
                    .builder()
                    .id(userId)
                    .lang(Locale.forLanguageTag(config.getLang()))
                    .mainState(BotState.START)
                    .botState(BotState.START)
                    .build());
        }

        AppUserDTO appUser = userDataCache.getAppUserData(userId);

//        BotState botState = BasicUtil.getCommandState(inputMsg);
//        appUser.setBotState(botState);

        return stateContext.processInputMessage(appUser.getMainState(), message);
    }


}
