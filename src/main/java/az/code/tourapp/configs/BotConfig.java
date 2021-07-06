package az.code.tourapp.configs;

import az.code.tourapp.TelegramBot;
import az.code.tourapp.components.TelegramFacade;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;

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
    public TelegramBot telegramBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setProxyHost(proxy.getHost());
        options.setProxyPort(proxy.getPort());
        options.setProxyType(proxy.getType());

        TelegramBot telegramBot = new TelegramBot(options, telegramFacade);
        telegramBot.setBotUsername(username);
        telegramBot.setBotToken(token);
        telegramBot.setBotPath(path);

        return telegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
