package az.code.tourapp.configs;

import az.code.tourapp.dtos.TimerInfoDTO;
import lombok.*;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
    private OfferProps offer;

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
        private String offerCap;
    }

    @Data
    public static class OfferProps {
        private ImageProps image;
        private TimeProps time;
        private Integer count;

        @Data
        public static class ImageProps {
            private String root;
            private String extension;
        }

        @Data
        public static class TimeProps {
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            private LocalTime start;
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            private LocalTime end;
        }
    }
}
