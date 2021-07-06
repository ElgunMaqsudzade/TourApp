package az.code.tourapp.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class EnumUtil {
    public static <T extends Enum<T>> Optional<T> valueOf(String string, Class<T> c) {
        Optional<T> result = Optional.empty();

        try {
            result = Optional.of(Enum.valueOf(c, string));
        } catch (IllegalArgumentException ex) {
            log.warn(string + " enum value couldn't found in -> " + c.getSimpleName() + " enum");
        } catch (Exception ex) {
            log.warn(string + " enum value is null");
        }
        return result;
    }
}