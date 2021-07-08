package az.code.tourapp.repos;

import az.code.tourapp.models.Action;
import az.code.tourapp.models.ActionInput;
import az.code.tourapp.models.BotState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ActionRepo extends JpaRepository<Action, Long> , JpaSpecificationExecutor<Action> {

}
