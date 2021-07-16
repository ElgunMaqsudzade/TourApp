package az.code.tourapp.dtos;

import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryDTO implements Serializable {
    private List<Reply> replyList;
    private List<BotState> botStates;
    private List<Locale> locales;
}
