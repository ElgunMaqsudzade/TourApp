package az.code.tourapp.repos;

import az.code.tourapp.models.BotState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotStateRepo extends JpaRepository<BotState, Long> {
    boolean existsByCommand(String command);

    BotState getBotStateByCommand(String command);
    BotState getByState(String state);
}
