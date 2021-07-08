package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.Action;
import az.code.tourapp.models.ActionInput;

import java.util.List;

public interface ActionDAO {

    Action getChosenAction(String currentState, String staticText);

    List<ActionInput> getActionInputList(String currentState, String locale);
}
