package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.enums.InputType;
import az.code.tourapp.models.Action;
import az.code.tourapp.models.ActionInput;
import az.code.tourapp.models.BotState;

import java.util.List;
import java.util.Optional;

public interface ActionDAO {

    Optional<Action> getAction(String currentState, String staticText, String locale);

    Optional<InputType> getInputType(String state);

    Optional<BotState> getNextActionState(String state);

    List<ActionInput> getActionInputList(String currentState, String locale);
}
