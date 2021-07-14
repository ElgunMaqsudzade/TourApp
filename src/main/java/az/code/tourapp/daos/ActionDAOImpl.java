package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.ActionDAO;
import az.code.tourapp.models.enums.InputType;
import az.code.tourapp.models.*;
import az.code.tourapp.repos.ActionInputRepo;
import az.code.tourapp.repos.ActionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ActionDAOImpl implements ActionDAO {
    ActionRepo actionRepo;
    ActionInputRepo actionInputRepo;

    public ActionDAOImpl(ActionRepo actionRepo, ActionInputRepo actionInputRepo) {
        this.actionRepo = actionRepo;
        this.actionInputRepo = actionInputRepo;
    }


    @Override
    public Optional<Action> getAction(String currentState, String staticText, String locale) {
        Specification<Action> actions = Specification.where((rt, q, cb) ->
                cb.equal(rt.get(Action_.CURRENT_STATE).get(BotState_.STATE), currentState));
        List<Action> action = actionRepo.findAll(actions);
        if (action.size() == 1) return action.stream().findFirst();
        if (action.size() > 1) {
            Optional<ActionInput> actionInput = getActionWithText(currentState, staticText, locale);
            if (actionInput.isEmpty()) return Optional.empty();
            return Optional.of(actionInput.get().getAction());
        }
        return Optional.empty();
    }

    @Override
    public Optional<InputType> getNextActionType(String state) {
        Specification<Action> spec = Specification.where((rt, q, cb) ->
                cb.equal(rt.get(Action_.currentState).get(BotState_.STATE), state));

        Optional<Action> actionInput = actionRepo.findAll(spec).stream().findFirst();
        if (actionInput.isEmpty()) {
            log.warn("Corresponding Action not found");
            return Optional.empty();
        }

        return Optional.of(actionInput.get().getInputType());
    }
    @Override
    public Optional<BotState> getNextActionState(String state) {
        Specification<Action> spec = Specification.where((rt, q, cb) ->
                cb.equal(rt.get(Action_.currentState).get(BotState_.STATE), state));

        Optional<Action> action = actionRepo.findAll(spec).stream().findFirst();
        if (action.isEmpty()) {
            log.warn("Corresponding Action not found");
            return Optional.empty();
        }

        return Optional.of(action.get().getNextState());
    }

    @Override
    public List<ActionInput> getActionInputList(String currentState, String locale) {

        Specification<ActionInput> spec = Specification.where((rt, q, cb) -> {
            Predicate lang = cb.or(cb.equal(rt.get(ActionInput_.LOCALE).get(Locale_.LANG), locale));
            Predicate state = cb.equal(rt.get(ActionInput_.ACTION).get(Action_.CURRENT_STATE).get(BotState_.STATE), currentState);
            return cb.and(lang, state);
        });
        return actionInputRepo.findAll(spec);
    }

    private Optional<ActionInput> getActionWithText(String currentState, String staticText, String locale) {
        Specification<ActionInput> spec = Specification.where((rt, q, cb) -> {
            Predicate text = cb.and(cb.equal(rt.get(ActionInput_.TEXT), staticText), cb.equal(rt.get(ActionInput_.LOCALE).get(Locale_.LANG), locale));
            Predicate notNull = cb.equal(rt.get(ActionInput_.ACTION).get(Action_.CURRENT_STATE).get(BotState_.STATE), currentState);
            return cb.and(text, notNull);
        });
        return actionInputRepo.findAll(spec).stream().findFirst();
    }
}
