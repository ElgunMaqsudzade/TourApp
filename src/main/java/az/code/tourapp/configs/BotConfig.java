package az.code.tourapp.configs;

import az.code.tourapp.MessageComponent;
import az.code.tourapp.TelegramWHBot;
import az.code.tourapp.components.TelegramFacade;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot", ignoreUnknownFields = false)
public class BotConfig {
    private String path;
    private String username;
    private String token;
    private String messageSource;
    private String lang;
    private BotProxyProps proxy;

    @Data
    public static class BotProxyProps {
        private DefaultBotOptions.ProxyType type;
        private String host;
        private int port;
    }

    @Bean
    public TelegramWHBot telegramWHBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setProxyHost(proxy.getHost());
        options.setProxyPort(proxy.getPort());
        options.setProxyType(proxy.getType());

        TelegramWHBot telegramWHBot = new TelegramWHBot(options, telegramFacade);
        telegramWHBot.setBotUsername(username);
        telegramWHBot.setBotToken(token);
        telegramWHBot.setBotPath(path);

        return telegramWHBot;
    }
    @Bean
    public MessageComponent messageComponent() {
        MessageComponent component = new MessageComponent();
        component.setBotUsername(username);
        component.setBotToken(token);
        component.setBotPath(path);

        return component;
    }

}
