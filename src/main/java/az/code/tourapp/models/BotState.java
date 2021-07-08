package az.code.tourapp.models;

import az.code.tourapp.dtos.DataType;
import lombok.*;
import org.checkerframework.checker.regex.RegexUtil;
import org.springframework.asm.Type;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.persistence.*;
import java.net.Proxy;
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
    @Enumerated(EnumType.STRING)
    private DataType dataType;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Reply> replyList;
}
