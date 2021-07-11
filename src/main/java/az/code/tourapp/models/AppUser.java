package az.code.tourapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @NotNull
    private long userId;
    @NotNull
    private long chatId;
    @OneToOne
    private Locale locale;
    private String uuid;
}
