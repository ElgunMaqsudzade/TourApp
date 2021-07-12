package az.code.tourapp;


import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.components.TelegramFacade;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.MessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;


@Getter
@Setter
@Component
public class TelegramWHBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    TelegramFacade telegramFacade;
    SchedulerExecutor sch;

    public TelegramWHBot(SchedulerExecutor sch, TelegramFacade facade, BotConfig config) {
        this.sch = sch;
        this.telegramFacade = facade;
        this.botPath = config.getPath();
        this.botUsername = config.getUsername();
        this.botToken = config.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }

    public void sendMessage(MessageDTO messageDTO) {
        sch.runSendMessageJob(messageDTO);
    }
}
