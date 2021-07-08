package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.dtos.InputType;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.*;
import az.code.tourapp.repos.ActionInputRepo;
import az.code.tourapp.repos.ActionRepo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ActionDAOImpl implements ActionDAO {
    ActionRepo actionRepo;
    ActionInputRepo actionInputRepo;

    public ActionDAOImpl(ActionRepo actionRepo, ActionInputRepo actionInputRepo) {
        this.actionRepo = actionRepo;
        this.actionInputRepo = actionInputRepo;
    }

    @Override
    public Action getChosenAction(String currentState, String staticText) {

        List<Action> actionList = getActionsWithoutBtn(currentState);
        if (actionList.size() > 0) return actionList.get(0);

        Specification<ActionInput> spec = Specification.where((rt, q, cb) -> {
            Predicate text = cb.or(cb.equal(rt.get(ActionInput_.TEXT), staticText));

            Predicate notNull = cb.equal(rt.get(ActionInput_.ACTION).get(Action_.CURRENT_STATE).get(BotState_.STATE), currentState);

            return cb.and(text, notNull);
        });

        Optional<ActionInput> actionInput = actionInputRepo.findAll(spec).stream().findFirst();
        if (actionInput.isEmpty()) throw new NotFound("Corresponding ActionInput not found");

        return actionInput.get().getAction();
    }

    @Override
    public List<ActionInput> getActionInputList(String currentState, String locale) {

        if (getActionsWithoutBtn(currentState).size() > 0) return new ArrayList<>();

        Specification<ActionInput> spec = Specification.where((rt, q, cb) -> {
            Predicate lang = cb.or(cb.equal(rt.get(ActionInput_.LOCALE).get(Locale_.LANG), locale));
            Predicate state = cb.equal(rt.get(ActionInput_.ACTION).get(Action_.CURRENT_STATE).get(BotState_.STATE), currentState);
            return cb.and(lang, state);
        });

        return actionInputRepo.findAll(spec);
    }

    private List<Action> getActionsWithoutBtn(String currentState) {
        Specification<Action> actionSpecification = Specification.where((rt, q, cb) ->
                cb.and(cb.equal(rt.get(Action_.CURRENT_STATE).get(BotState_.STATE), currentState),
                        cb.or(cb.equal(rt.get(Action_.INPUT_TYPE), InputType.FIELD),
                                cb.isNull(rt.get(Action_.INPUT_TYPE)))));

        return actionRepo.findAll(actionSpecification);
    }
}
