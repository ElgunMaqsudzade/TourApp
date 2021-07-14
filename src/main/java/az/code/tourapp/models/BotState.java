package az.code.tourapp.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity(name = "bot_states")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BotState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;
    private String command;
    private String regex;
    private boolean isSavable;
    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Reply> replyList;
}
