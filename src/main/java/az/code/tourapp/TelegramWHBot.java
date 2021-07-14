package az.code.tourapp;


import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.components.TelegramFacade;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.OfferDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


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

    public void sendMessage(OfferDTO offerDTO) {
        sch.runSendMessageJob(offerDTO);
    }
}
