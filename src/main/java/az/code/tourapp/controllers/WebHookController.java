package az.code.tourapp.controllers;

import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.components.SendMessageComponent;
import az.code.tourapp.components.TelegramWHBot;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.exceptions.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@Slf4j
public class WebHookController {
    TelegramWHBot telegramWHBot;
    SendMessageComponent sender;

    public WebHookController(TelegramWHBot telegramWHBot, SendMessageComponent sender) {
        this.telegramWHBot = telegramWHBot;
        this.sender = sender;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<String>  errorHandler(Error ex) {
        log.warn(ex.getMessage());
        sender.sendMessage(ex.getMessageData());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<String>  notFoundHandler(NotFound ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramWHBot.onWebhookUpdateReceived(update);
    }

}
