package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;
import az.code.tourapp.repos.AppUserRepo;
import az.code.tourapp.repos.BotStateRepo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppUserDAOImpl implements AppUserDAO {
    BotStateRepo stateRepo;
    AppUserRepo appUserRepo;

    public AppUserDAOImpl(BotStateRepo stateRepo, AppUserRepo appUserRepo) {
        this.stateRepo = stateRepo;
        this.appUserRepo = appUserRepo;
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return appUserRepo.findById(id);
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
}
