package az.code.tourapp.dtos;

import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.enums.BasicState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDTO implements Serializable {
    private BasicState mainState;
    private BotState state;
    private Locale lang;
    private Map<String, String> subscription;
}
