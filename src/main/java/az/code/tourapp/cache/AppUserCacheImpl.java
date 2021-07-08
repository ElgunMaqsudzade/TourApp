package az.code.tourapp.cache;


import az.code.tourapp.daos.interfaces.LocaleDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;
import az.code.tourapp.repos.AppUserRepo;
import az.code.tourapp.repos.LocaleRepo;
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
    AppUserRepo appUserRepo;
    LocaleDAO localeDAO;

    private final String STATE = "STATE";
    private final String LOCALE = "LOCALE";
    private final String MAIN_STATE = "MAINSTATE";

    public AppUserCacheImpl(AppUserRepo appUserRepo, LocaleDAO localeDAO) {
        this.appUserRepo = appUserRepo;
        this.localeDAO = localeDAO;
    }

    @Override
    public void setAppUserBotState(Long userId, BotState botState) {
        Map<String, String> user = getAppUserData(userId);
        user.put(STATE, botState.getState());
    }

    @Override
    public void removeAppUser(Long userId) {
        userMap.remove(userId);
    }

    @Override
    public String getBotState(Long userId) {
        if (getAppUserData(userId).get(STATE).isEmpty()) throw new NotFound("Corresponding state not found");

        return getAppUserData(userId).get(STATE);
    }

    @Override
    public String getMainState(Long userId) {
        return getAppUserData(userId).get(MAIN_STATE);
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
        appUserRepo.save(AppUser.builder().userId(userId).locale(localeDAO.findByLang(userData.get(LOCALE))).build());
    }
}
