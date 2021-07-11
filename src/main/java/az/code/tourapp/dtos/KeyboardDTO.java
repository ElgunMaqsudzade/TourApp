package az.code.tourapp.dtos;

import az.code.tourapp.enums.InputType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class KeyboardDTO {
    private InputType inputType;
    private String text;
    private String callbackData;
}