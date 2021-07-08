package az.code.tourapp.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "replies")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne(fetch = FetchType.EAGER)
    private BotState state;
    @ManyToOne
    private Locale locale;
}
