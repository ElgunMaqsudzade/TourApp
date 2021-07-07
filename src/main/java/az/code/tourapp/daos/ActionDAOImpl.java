package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.Action;
import az.code.tourapp.repos.ActionRepo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Component
public class ActionDAOImpl implements ActionDAO {
    ActionRepo actionRepo;

    public ActionDAOImpl(ActionRepo actionRepo) {
        this.actionRepo = actionRepo;
    }

    @Override
    public Action getChosenAction(String currentState, String staticText) {


        Specification<Action> spec = Specification.where((rt, q, cb) -> {
            Predicate textNullOrEqual = cb.or(cb.isNull(rt.get("staticText")),
                    cb.equal(rt.get("staticText"), staticText));
            Predicate notNull = cb.equal(rt.get("currentState").get("state"), currentState);

            return cb.and(textNullOrEqual, notNull);
        });

        Optional<Action> action = actionRepo.findAll(spec).stream().findFirst();
        if (action.isEmpty()) throw new NotFound("Corresponding Action not found");

        return action.get();
    }

    @Override
    public List<Action> getActionList(String currentState) {
        Specification<Action> actionSpecification = Specification.where((rt, q, cb) ->
                cb.equal(rt.get("currentState").get("state"), currentState));

        return actionRepo.findAll(actionSpecification);
    }
}
