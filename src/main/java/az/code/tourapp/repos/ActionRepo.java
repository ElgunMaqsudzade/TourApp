package az.code.tourapp.repos;

import az.code.tourapp.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionRepo extends JpaRepository<Action, Long> , JpaSpecificationExecutor<Action> {
}
