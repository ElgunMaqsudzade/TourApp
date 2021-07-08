package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;

import java.util.Optional;

public interface AppUserDAO {
    Optional<AppUser> findById(Long id);

    BotState getStateByCommand(String command);

    BotState getState(String state);

    boolean existsCommand(String state);
}
