package az.code.tourapp.components.keyboards;

import az.code.tourapp.components.interfaces.ButtonGenerator;
import az.code.tourapp.models.enums.InputType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Map;

@Getter
@Setter
@Component
public class ContactButtonGenerator implements ButtonGenerator<ReplyKeyboardMarkup> {
    private boolean selective = true;
    private boolean onetime = true;
    private boolean resize = true;

    @Override
    public ReplyKeyboardMarkup generateButtons(Map<String, String> data) {
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton button = data
                .keySet()
                .stream()
                .map(KeyboardButton::new)
                .peek(e -> e.setRequestContact(true))
                .findAny().orElse(null);
        keyboardFirstRow.add(button);
        return ReplyKeyboardMarkup
                .builder()
                .keyboardRow(keyboardFirstRow)
                .selective(selective)
                .resizeKeyboard(resize)
                .oneTimeKeyboard(onetime)
                .build();
    }

    @Override
    public InputType getMainType() {
        return InputType.CONTACT;
    }
}
