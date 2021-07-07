package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.Action;

import java.util.List;

public interface ActionDAO {


    Action getChosenAction(String currentState, String staticText);

    List<Action> getActionList(String currentState);
}
