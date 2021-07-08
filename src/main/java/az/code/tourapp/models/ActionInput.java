package az.code.tourapp.models;

import az.code.tourapp.dtos.InputType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActionInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    private Locale locale;
    @ManyToOne(fetch = FetchType.EAGER)
    private Action action;
}
