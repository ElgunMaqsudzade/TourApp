package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.dtos.InputType;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.Reply;
import az.code.tourapp.repos.ActionRepo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ActionDAOImpl implements ActionDAO {
    ActionRepo actionRepo;

    public ActionDAOImpl(ActionRepo actionRepo) {
        this.actionRepo = actionRepo;
    }

    @Override
    public Action getAction(String currentState, String staticText) {

        Specification<Action> actionSpecification = Specification.where((rt, q, cb) -> cb
                .or(cb.equal(rt.get("currentState").get("state"), currentState),
                        cb.equal(rt.get("staticText"), staticText)));

        Optional<Action> action = actionRepo.findAll(actionSpecification).stream().findFirst();
        if (action.isEmpty()) throw new NotFound("Corresponding Action not found");

        return action.get();
    }

    @Override
    public List<Action> getNextActionList(Action currentAction) {
        Specification<Action> actionSpecification = Specification.where((rt, q, cb) ->
                cb.equal(rt.get("currentState"), currentAction.getNextState()));

        return actionRepo.findAll(actionSpecification);
    }
}
