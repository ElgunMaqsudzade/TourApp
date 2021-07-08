package az.code.tourapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    private long userId;
    private long chatId;
    @OneToOne
    private Locale locale;
    private final String uuid = UUID.randomUUID().toString();
}
