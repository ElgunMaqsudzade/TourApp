package az.code.tourapp.repos;

import az.code.tourapp.models.ActionInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ActionInputRepo extends JpaRepository<ActionInput, Long>, JpaSpecificationExecutor<ActionInput> {

}