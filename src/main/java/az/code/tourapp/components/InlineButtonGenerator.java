package az.code.tourapp.components;

import az.code.tourapp.components.interfaces.ButtonGenerator;
import az.code.tourapp.enums.InputType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InlineButtonGenerator implements ButtonGenerator<InlineKeyboardMarkup> {

    @Override
    public InlineKeyboardMarkup generateButtons(Map<String, String> data) {
        List<InlineKeyboardButton> keyboardButtons = data.keySet()
                .stream()
                .map(i -> InlineKeyboardButton
                        .builder()
                        .callbackData(i)
                        .text(data.get(i))
                        .build())
                .collect(Collectors.toList());

        return InlineKeyboardMarkup.builder().keyboardRow(keyboardButtons).build();
    }

    @Override
    public InputType getMainType() {
        return InputType.BUTTON;
    }
}
