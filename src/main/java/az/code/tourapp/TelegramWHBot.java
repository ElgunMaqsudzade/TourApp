package az.code.tourapp;


import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.components.TelegramFacade;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.configs.RabbitMQConfig;
import az.code.tourapp.models.Offer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
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

    @RabbitListener(queues = RabbitMQConfig.offer)
    public void sendMessage(Offer offer) {
        sch.runHandleOfferJob(offer);
    }
}
