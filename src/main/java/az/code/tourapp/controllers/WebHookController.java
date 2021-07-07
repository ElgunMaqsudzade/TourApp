package az.code.tourapp.controllers;

import az.code.tourapp.TelegramWHBot;
import az.code.tourapp.exceptions.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@Slf4j
public class WebHookController {
    private final TelegramWHBot telegramWHBot;

    public WebHookController(TelegramWHBot telegramWHBot) {
        this.telegramWHBot = telegramWHBot;
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<String> handlerNotFoundException(NotFound ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramWHBot.onWebhookUpdateReceived(update);
    }

}
