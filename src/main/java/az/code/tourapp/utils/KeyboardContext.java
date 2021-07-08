package az.code.tourapp.utils;

import az.code.tourapp.components.interfaces.ButtonGenerator;
import az.code.tourapp.dtos.InputType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
public class KeyboardContext {
    private final Map<InputType, ButtonGenerator> buttonGenerator = new HashMap<>();

    public KeyboardContext(List<ButtonGenerator> buttonGenerators) {
        buttonGenerators.forEach(i -> this.buttonGenerator.put(i.getMainType(), i));
    }


    public ReplyKeyboard generateKeyboard(Map<String, InputType> map) {
        if (map.isEmpty()) return null;

        InputType inputType = map.keySet().stream().map(map::get).findFirst().orElseThrow();

        Map<String, String> buttonMap = map.keySet().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));

        return (ReplyKeyboard) buttonGenerator.get(inputType).generateButtons(buttonMap);
    }
}
