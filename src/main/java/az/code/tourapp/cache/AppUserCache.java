package az.code.tourapp.cache;


import az.code.tourapp.enums.BasicState;
import az.code.tourapp.models.BotState;

import java.util.Map;


public interface AppUserCache {

    void setState(Long userId, String state);


    boolean saveData(Long userId, String state, String data);

    void setMainState(Long userId, BasicState mainState);

    void setLocale(Long userId, String locale);

    void createUserData(Long userId,Long chatId);

    void removeUserData(Long userId, Long chatId, String locale);

    String getBotState(Long userId);

    String getLocale(Long userId);

    String getMainState(Long userId);

    boolean existsById(Long userId);

    Map<String, String> getUserData(Long userId);

    void updateUserData(Long userId, Map<String, String> userData);
}
