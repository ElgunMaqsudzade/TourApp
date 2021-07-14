package az.code.tourapp.services;

import az.code.tourapp.models.enums.BasicState;

import java.util.Map;

public interface AppUserCacheService {
    boolean saveData(Long userId, String state, String data);

    void setState(Long userId, String state);


    void setMainState(Long userId, BasicState mainState);

    void setLocale(Long userId, String locale);


    String getBotState(Long userId);

    String getLocale(Long userId);

    String getMainState(Long userId);

    boolean existsById(Long userId);

    void delete(Long userId, Long chatId, String locale);

    Map<String, String> findById(Long userId);

    void updateSub(Long userId, Map<String, String> userData);

    void updateAction(Long userId, Map<String, String> userData);

    void create(Long userId, Long chatId);
}
