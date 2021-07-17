package az.code.tourapp.services.interfaces;

import az.code.tourapp.dtos.UserDataDTO;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.enums.BasicState;

import java.util.Map;

public interface SubCacheService {
    boolean saveData(Long userId, String state, String data);

    void setState(Long userId, String state);


    void setMainState(Long userId, BasicState mainState);

    void setLocale(Long userId, String locale);


    BotState getBotState(Long userId);

    String getLocale(Long userId);

    String getMainState(Long userId);

    boolean existsById(Long userId);

    boolean existsSubById(Long userId);

    void delete(Long userId, Long chatId);

    void deleteSubscription(Long userId);

    UserDataDTO findById(Long userId);

    void update(Long userId, UserDataDTO userData);

    void create(Long userId, Long chatId);
}
