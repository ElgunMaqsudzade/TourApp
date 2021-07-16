package az.code.tourapp.configs;

import az.code.tourapp.dtos.TimerInfoDTO;
import lombok.*;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
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
    private Redis redis;

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

    @Data
    public static class Redis {
        private String subscription;
        private String dictionary;
        private String offer;
    }
}
