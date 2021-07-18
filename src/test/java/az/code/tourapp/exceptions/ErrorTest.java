package az.code.tourapp.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class ErrorTest {

    @Test
    void whenErrorThrown() {
        //given
        String message = "salam";
        Long userId = 123324L;
        //when
        Throwable thrown = catchThrowable(() -> { throw new Error(message,userId); });
        //expected
        assertThat(thrown).hasMessageContaining(message);
    }
}