package az.code.tourapp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class NotFound extends RuntimeException {

    public NotFound(String message) {
        super(message);
    }
}