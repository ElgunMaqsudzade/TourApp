package az.code.tourapp.models;

import az.code.tourapp.enums.InputType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private InputType inputType;
    @ManyToOne(fetch = FetchType.EAGER)
    private BotState currentState;
    @OneToMany(mappedBy = "action", cascade=CascadeType.ALL)
    private List<ActionInput> actionInputs;
    @ManyToOne
    private BotState nextState;
}
