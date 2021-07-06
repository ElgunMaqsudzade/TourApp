package az.code.tourapp.controllers;

import az.code.tourapp.TelegramBot;
import az.code.tourapp.exceptions.NotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final TelegramBot telegramBot;

    public WebHookController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<String> handlerNotFoundException(NotFound ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }

}