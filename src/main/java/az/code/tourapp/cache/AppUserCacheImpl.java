package az.code.tourapp.cache;


import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * userMap: user_id  and user's data.
 */

@Component
public class AppUserCacheImpl implements AppUserCache {
    private final Map<Long, Map<String, String>> userMap = new HashMap<>();


    @Override
    public void setAppUserBotState(Long userId, BotState botState) {
        Map<String, String> user = getAppUserData(userId);
        user.put("STATE", botState.getState());
    }

    @Override
    public String getAppUserBotState(Long userId) {
        if (getAppUserData(userId).get("STATE").isEmpty()) throw new NotFound("Corresponding state not found");

        return getAppUserData(userId).get("STATE");
    }

    @Override
    public boolean existsById(Long userId) {
        return getAppUserData(userId) != null;
    }

    @Override
    public Map<String, String> getAppUserData(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public void saveAppUserData(Long userId, Map<String, String> userData) {
        userMap.put(userId, userData);
    }
}
