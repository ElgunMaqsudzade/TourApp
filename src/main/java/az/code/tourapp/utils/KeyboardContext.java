package az.code.tourapp.utils;

import az.code.tourapp.components.interfaces.ButtonGenerator;
import az.code.tourapp.dtos.KeyboardDTO;
import az.code.tourapp.models.enums.InputType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
public class KeyboardContext {
    private final Map<InputType, ButtonGenerator> buttonGenerator = new HashMap<>();

    public KeyboardContext(List<ButtonGenerator> buttonGenerators) {
        buttonGenerators.forEach(i -> this.buttonGenerator.put(i.getMainType(), i));
    }


    public ReplyKeyboard generateKeyboard(List<KeyboardDTO> dtos) {
        if (dtos.isEmpty()) return null;

        InputType inputType = dtos.stream().map(KeyboardDTO::getInputType).findFirst().orElseThrow();

        Map<String, String> buttonMap = dtos.stream().collect(Collectors.toMap(KeyboardDTO::getText, KeyboardDTO::getText));

        return (ReplyKeyboard) buttonGenerator.get(inputType).generateButtons(buttonMap);
    }
}
