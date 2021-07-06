package az.code.tourapp.cache;

import az.code.tourapp.dtos.BotState;
import az.code.tourapp.models.AppUser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * userMap: user_id  and user's data.
 */

@Component
public class UserDataCache implements DataCache {
    private final Map<Long, AppUser> userMap = new HashMap<>();

    @Override
    public void setAppUserBotState(Long userId, BotState botState) {
        AppUser user = getAppUserData(userId);
        saveAppUserData(userId, user.toBuilder().botState(botState).build());
    }

    @Override
    public BotState getAppUserBotState(Long userId) {
        return getAppUserData(userId).getBotState();
    }

    @Override
    public AppUser getAppUserData(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public void saveAppUserData(Long userId, AppUser appUser) {
        userMap.put(userId, appUser);
    }
}
