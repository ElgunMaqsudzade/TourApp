package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;

import java.util.List;

public interface AppUserDAO {
    AppUser findById(Long id);
    boolean existsById(Long id);

    AppUser save(AppUser appUser);

    BotState getStateByCommand(String command);

    BotState getState(String state);

    boolean existsCommand(String state);

    Locale findLang(String lang);

    List<Locale> getAllLanguages();
}
