package az.code.tourapp.daos.interfaces;

import az.code.tourapp.dtos.InputType;
import az.code.tourapp.models.Action;

import java.util.List;

public interface ActionDAO {

    Action getAction(String currentState, String staticText);

    List<Action> getNextActionList(Action currentAction);
}
