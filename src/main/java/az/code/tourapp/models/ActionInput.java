package az.code.tourapp.models;

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
    @ManyToOne(optional = false)
    private Locale locale;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Action action;
}
