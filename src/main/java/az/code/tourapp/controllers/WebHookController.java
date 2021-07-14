package az.code.tourapp.controllers;

import az.code.tourapp.components.MessageSender;
import az.code.tourapp.TelegramWHBot;
import az.code.tourapp.dtos.OfferDTO;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.exceptions.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.Valid;

@RestController
@Slf4j
public class WebHookController {
    TelegramWHBot telegramWHBot;
    MessageSender sender;

    public WebHookController(TelegramWHBot telegramWHBot, MessageSender sender) {
        this.telegramWHBot = telegramWHBot;
        this.sender = sender;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<String> errorHandler(Error ex) {
        log.warn(ex.getMessage());
        sender.sendMessage(ex.getMessageData());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<String> notFoundHandler(NotFound ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramWHBot.onWebhookUpdateReceived(update);
    }

    @RequestMapping(value = "/api/v1/send", method = RequestMethod.POST)
    public ResponseEntity<String> sendMessage(@Valid @ModelAttribute OfferDTO offerDTO) {
        telegramWHBot.sendMessage(offerDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
