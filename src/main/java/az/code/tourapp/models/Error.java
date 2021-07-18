package az.code.tourapp.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "errors")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Error implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String errorMessage;
    @ManyToOne(fetch = FetchType.EAGER)
    private BotState state;
    @ManyToOne(optional = false)
    private Locale locale;
}
