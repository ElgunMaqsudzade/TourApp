package az.code.tourapp.cache;


import az.code.tourapp.models.BotState;

import java.util.Map;


public interface AppUserCache {

    void setAppUserBotState(Long userId, BotState botState);

    String getAppUserBotState(Long userId);

    boolean existsById(Long userId);

    Map<String, String> getAppUserData(Long userId);

    void saveAppUserData(Long userId, Map<String, String> userData);
}
