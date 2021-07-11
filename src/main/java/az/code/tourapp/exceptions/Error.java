package az.code.tourapp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class Error extends RuntimeException {
    private final SendMessage messageData;

    public Error(String message, Long chatId) {
        super(message);
        this.messageData = new SendMessage(chatId.toString(), message);
    }
}