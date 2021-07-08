package az.code.tourapp.components.interfaces;

import az.code.tourapp.dtos.InputType;

import java.util.Map;

public interface ButtonGenerator<T> {
    T generateButtons(Map<String, String> data);

    InputType getMainType();
}
