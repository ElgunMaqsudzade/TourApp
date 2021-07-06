package az.code.tourapp.dtos;

import az.code.tourapp.dtos.BotState;
import az.code.tourapp.dtos.InputType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionDTO {
    private Long id;
    private BotState currentState;
    private InputType type;
    private String staticText;
    private BotState nextState;
}
