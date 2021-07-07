package az.code.tourapp.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

public class InlineKeyboardUtil{

    public static InlineKeyboardMarkup getInlineButtons(List<String> strings) {
        List<InlineKeyboardButton> keyboardButtons = strings
                .stream()
                .map(i -> InlineKeyboardButton.builder().callbackData(i).text(i).build())
                .collect(Collectors.toList());

        return InlineKeyboardMarkup.builder().keyboardRow(keyboardButtons).build();
    }
}
