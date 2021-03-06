package az.code.tourapp.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "replies")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Reply implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne(fetch = FetchType.EAGER)
    private BotState state;
    @ManyToOne(optional = false)
    private Locale locale;
}
