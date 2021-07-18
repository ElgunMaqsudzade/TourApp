package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.repos.AppUserRepo;
import az.code.tourapp.repos.BotStateRepo;
import az.code.tourapp.repos.LocaleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AppUserDAOImpl implements AppUserDAO {
    BotStateRepo stateRepo;
    AppUserRepo appUserRepo;
    LocaleRepo localeRepo;

    public AppUserDAOImpl(BotStateRepo stateRepo, AppUserRepo appUserRepo, LocaleRepo localeRepo) {
        this.stateRepo = stateRepo;
        this.appUserRepo = appUserRepo;
        this.localeRepo = localeRepo;
    }

    @Override
    public AppUser findById(Long id) {
        Optional<AppUser> user = appUserRepo.findById(id);
        if(user.isEmpty()) throw new NotFound("User not found by id");

        return user.get();
    }

    @Override
    public List<AppUser> findAll() {
        return appUserRepo.findAll();
    }

    @Override
    public AppUser findByUUID(String uuid) {
        Optional<AppUser> user = appUserRepo.findByUuid(uuid);
        if(user.isEmpty()) throw new NotFound("User not found by uuid");

        return user.get();
    }

    @Override
    public boolean existsById(Long id) {
        return appUserRepo.existsById(id);
    }

    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepo.save(appUser);
    }

    @Override
    public BotState getStateByCommand(String command) {
        return stateRepo.getBotStateByCommand(command);
    }

    @Override
    public BotState getState(String state) {
        return stateRepo.getByState(state);
    }

    @Override
    public boolean existsCommand(String command) {

        return stateRepo.existsByCommand(command);
    }

    @Override
    public Locale findLang(String lang) {
        Optional<Locale> locale = localeRepo.findByLang(lang);
        if (locale.isEmpty()) throw new NotFound("Language couldn't found");
        return locale.get();
    }
    @Override
    public List<Locale> getAllLanguages() {
        return localeRepo.findAll();
    }
}
