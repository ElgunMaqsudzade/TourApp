package az.code.tourapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "bot_states")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BotState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
    private List<Reply> replyList;
}
