package az.code.tourapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.Optional;

@Slf4j
public class EnumUtil {
    public static <T extends Enum<T>> Optional<T> valueOf(String string, Class<T> c) {
        try {
            return Optional.of(EnumUtils.findEnumInsensitiveCase(c, string));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static <T extends Enum<T>> Optional<T> commandToEnum(String command, Class<T> c) {
        String string = command.substring(1);
        return valueOf(string, c);
    }
}
