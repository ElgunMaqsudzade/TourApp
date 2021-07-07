package az.code.tourapp.models;

import az.code.tourapp.dtos.InputType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private BotState currentState;
    @Enumerated(EnumType.STRING)
    private InputType inputType;
    private String staticText;
    @ManyToOne
    private BotState nextState;
}
