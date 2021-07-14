package az.code.tourapp.configs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.List;

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
    private IgnoreProps ignore;

    @Data
    public static class BotProxyProps {
        private DefaultBotOptions.ProxyType type;
        private String host;
        private int port;
    }
    @Data
    public static class IgnoreProps {
        private List<String> save;
        private String hard;
    }
}
