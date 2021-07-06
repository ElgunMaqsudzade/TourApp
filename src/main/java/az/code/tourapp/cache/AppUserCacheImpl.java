package az.code.tourapp.cache;

import az.code.tourapp.dtos.BotState;
import az.code.tourapp.dtos.AppUserDTO;
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
    private final Map<Long, AppUserDTO> userMap = new HashMap<>();

    @Override
    public void setAppUserBotState(Long userId, BotState botState) {
        AppUserDTO user = getAppUserData(userId);
        saveAppUserData(userId, user.toBuilder().botState(botState).build());
    }

    @Override
    public BotState getAppUserBotState(Long userId) {
        return getAppUserData(userId).getBotState();
    }

    @Override
    public boolean existsById(Long userId) {
        return getAppUserData(userId) != null;
    }

    @Override
    public AppUserDTO getAppUserData(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public void saveAppUserData(Long userId, AppUserDTO appUser) {
        userMap.put(userId, appUser);
    }
}
