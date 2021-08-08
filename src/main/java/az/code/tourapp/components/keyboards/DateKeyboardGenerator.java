package az.code.tourapp.components.keyboards;

import az.code.tourapp.components.interfaces.ButtonGenerator;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.KeyboardDTO;
import az.code.tourapp.models.enums.InputType;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DateKeyboardGenerator implements ButtonGenerator<InlineKeyboardMarkup> {
    public static final String[] WD = {"M", "T", "W", "T", "F", "S", "S"};

    private final String IGNORE;
    private final List<String> ignores;

    public DateKeyboardGenerator(BotConfig config) {
        this.IGNORE = config.getIgnore().getHard();
        this.ignores = config.getIgnore().getSave();
    }

    @Override
    public InlineKeyboardMarkup generateButtons(Map<String, String> data) {
        InlineKeyboardMarkup keyboardMarkup = null;
        String usersAnswer = data.keySet().stream().findFirst().get();
        if (ignores.stream().anyMatch(usersAnswer::contains)) {
            if (usersAnswer.contains(ignores.get(0)))
                keyboardMarkup = generateKeyboard(LocalDate.parse(usersAnswer.substring(2)).plusDays(30));
            else if (usersAnswer.contains(ignores.get(1))) {
                keyboardMarkup = generateKeyboard(LocalDate.parse(usersAnswer.substring(2)).minusDays(30));
            }
        } else {
            keyboardMarkup = generateKeyboard(LocalDate.now());
        }
        return keyboardMarkup;
    }

    public InlineKeyboardMarkup generateKeyboard(LocalDate date) {
        if (date == null) {
            return null;
        }

        List<List<KeyboardDTO>> keyboard = new ArrayList<>();

        // row - Month and Year
        List<KeyboardDTO> headerRow = new ArrayList<>();
        headerRow.add(createButton(IGNORE, new SimpleDateFormat("MMM yyyy").format(date.toDate())));
        keyboard.add(headerRow);

        // row - Days of the week
        List<KeyboardDTO> daysOfWeekRow = new ArrayList<>();
        for (String day : WD) {
            daysOfWeekRow.add(createButton(IGNORE, day));
        }
        keyboard.add(daysOfWeekRow);

        LocalDate firstDay = date.dayOfMonth().withMinimumValue();

        int shift = firstDay.dayOfWeek().get() - 1;
        int daysInMonth = firstDay.dayOfMonth().getMaximumValue();
        int rows = ((daysInMonth + shift) % 7 > 0 ? 1 : 0) + (daysInMonth + shift) / 7;
        for (int i = 0; i < rows; i++) {
            keyboard.add(buildRow(firstDay, shift, IGNORE));
            firstDay = firstDay.plusDays(7 - shift);
            shift = 0;
        }

        List<KeyboardDTO> controlsRow = new ArrayList<>();
        if (date.isAfter(LocalDate.now()))
            controlsRow.add(createButton("<|" + date, "<"));
        controlsRow.add(createButton(">|" + date, ">"));
        keyboard.add(controlsRow);

        List<List<InlineKeyboardButton>> keyboardBtn = keyboard
                .stream()
                .map(i -> i.stream()
                        .map(a -> InlineKeyboardButton
                                .builder()
                                .text(a.getText())
                                .callbackData(a.getCallbackData())
                                .build())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        return InlineKeyboardMarkup.builder().keyboard(keyboardBtn).build();
    }

    private KeyboardDTO createButton(String callBack, String text) {
        return KeyboardDTO.builder().callbackData(callBack).text(text).build();
    }

    public List<KeyboardDTO> buildRow(LocalDate date, int shift, String IGNORE) {
        List<KeyboardDTO> row = new ArrayList<>();
        int day = date.getDayOfMonth();
        LocalDate callbackDate = date;
        for (int j = 0; j < shift; j++) {
            row.add(createButton(IGNORE, " "));
        }
        for (int j = shift; j < 7; j++) {
            if (day <= (date.dayOfMonth().getMaximumValue())) {
                row.add(createButton(callbackDate.toString(), Integer.toString(day++)));
                callbackDate = callbackDate.plusDays(1);
            } else {
                row.add(createButton(IGNORE, " "));
            }
        }
        return row;
    }

    @Override
    public InputType getMainType() {
        return InputType.DATE;
    }
}
