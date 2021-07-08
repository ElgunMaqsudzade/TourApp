package az.code.tourapp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class Error extends RuntimeException {
    private final Long chatId;

    public Error(String message, Long chatId) {
        super(message);
        this.chatId = chatId;
    }
}