package az.code.tourapp.components.keyboards;

import az.code.tourapp.components.interfaces.ButtonGenerator;
import az.code.tourapp.models.enums.InputType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class KeyBoardGenerator implements ButtonGenerator<ReplyKeyboardMarkup> {
    private boolean selective = true;
    private boolean onetime = true;
    private boolean resize = true;
    private Integer maxColumn = 3;


    @Override
    public ReplyKeyboardMarkup generateButtons(Map<String, String> data) {
        List<KeyboardButton> buttons = data
                .keySet()
                .stream()
                .map(KeyboardButton::new)
                .collect(Collectors.toList());

        List<KeyboardRow> rows = new ArrayList<>();

        if (buttons.size() > this.maxColumn) {
            buttons.forEach(i -> {
                KeyboardRow row = new KeyboardRow();
                row.add(i);
                rows.add(row);
            });
        } else {
            KeyboardRow row = new KeyboardRow();
            row.addAll(buttons);
            rows.add(row);
        }
        return ReplyKeyboardMarkup
                .builder()
                .keyboard(rows)
                .selective(selective)
                .resizeKeyboard(resize)
                .oneTimeKeyboard(onetime)
                .build();
    }

    @Override
    public InputType getMainType() {
        return InputType.KEYBOARD;
    }
}
