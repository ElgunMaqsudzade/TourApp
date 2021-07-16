package az.code.tourapp.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity(name = "bot_states")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BotState implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;
    private String command;
    private String regex;
    private boolean isSavable;
}
