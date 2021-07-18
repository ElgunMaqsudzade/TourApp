package az.code.tourapp.components.keyboards;

import az.code.tourapp.dtos.KeyboardDTO;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
class DateKeyboardGeneratorTest {

    @Autowired
    DateKeyboardGenerator dateKeyboardGenerator;

    @Test
    void buildRow() {
        //given
        LocalDate localDate = LocalDate.parse("2000-06-20");
        String ignore = "dsa";
        int shift = 1;
        //when
        List<KeyboardDTO> keyboardDTOS = dateKeyboardGenerator.buildRow(localDate, shift, ignore);
        //expected
        List<KeyboardDTO> expected = new ArrayList<>();
        expected.add(KeyboardDTO.builder().text(" ").callbackData(ignore).build());
        expected.addAll(IntStream.range(0, 6)
                .mapToObj(i -> KeyboardDTO
                        .builder()
                        .text(String.valueOf(localDate.getDayOfMonth() + i))
                        .callbackData(localDate.plusDays(i).toString())
                        .build())
                .collect(Collectors.toList()));

        assertThat(expected).isEqualTo(keyboardDTOS);
    }
}