package az.code.tourapp.models;


import az.code.tourapp.dtos.BotState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private BotState botState;
    @Enumerated(EnumType.STRING)
    private BotState mainState;
}
