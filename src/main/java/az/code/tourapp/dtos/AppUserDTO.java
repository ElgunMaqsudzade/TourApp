package az.code.tourapp.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {
    private Long id;
    private String name;
    private Locale lang;
    private BotState botState;
    private BotState mainState;
}
