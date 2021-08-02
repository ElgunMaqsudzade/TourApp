package az.code.tourapp.controllers;

import az.code.tourapp.TelegramWHBot;
import az.code.tourapp.components.WebhookBotComponent;
import az.code.tourapp.exceptions.Error;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.Offer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WebHookController {
    private final TelegramWHBot telegramWHBot;
    private final WebhookBotComponent sender;
    private final RabbitTemplate temp;


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exHandler(Exception ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<String> errorHandler(Error ex) {
        log.warn(ex.getMessage());
        sender.sendMessage(ex.getMessageData());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> notFoundHandler(NotFound ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramWHBot.onWebhookUpdateReceived(update);
    }

    @RequestMapping(value = "/api/v1/send", method = RequestMethod.POST)
    public ResponseEntity<?> offerFromAgent(@RequestParam MultipartFile file, @Valid @ModelAttribute Offer offer) throws IOException {
        temp.convertAndSend(offer.toBuilder().fileAsBytes(file.getBytes()).build());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
