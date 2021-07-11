package az.code.tourapp.cache;


import az.code.tourapp.components.SendMessageComponent;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.enums.BasicCache;
import az.code.tourapp.enums.BasicState;
import az.code.tourapp.enums.InputType;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * userMap: user_id  and user's data.
 */

@Component
public class AppUserCacheImpl implements AppUserCache {
    private final Map<Long, Map<String, String>> userMap = new HashMap<>();
    AppUserDAO appUserDAO;
    BotConfig config;
    SendMessageComponent sender;
    ReplyDAO replyDAO;

    public AppUserCacheImpl( AppUserDAO appUserDAO, BotConfig config, SendMessageComponent sender, ReplyDAO replyDAO) {
        this.appUserDAO = appUserDAO;
        this.config = config;
        this.sender = sender;
        this.replyDAO = replyDAO;
    }

    @Override
    public void setState(Long userId, String state) {
        Map<String, String> user = getUserData(userId);
        user.put(BasicCache.STATE.toString(), state);
        updateUserData(userId, user);
    }

    @Override
    public boolean saveData(Long userId, String botState, String data) {
        BotState state = appUserDAO.getState(botState);
        Map<String, String> user = getUserData(userId);

        String regex = state.getRegex();

        if (botState.equals(BasicCache.LOCALE.toString())) {
            setLocale(userId, data);
            return true;
        } else if (regex != null) {
            if (regex.equals(InputType.DATE.toString())) {
                try {
                    LocalDate.parse(data);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            } else if (data.matches(regex)) {
                user.put(state.getState(), data);
            } else {
                return false;
            }
        }
        updateUserData(userId, user);
        return true;
    }

    @Override
    public void setMainState(Long userId, BasicState mainState) {
        Map<String, String> user = getUserData(userId);
        user.put(BasicCache.MAIN_STATE.toString(), mainState.toString());
        updateUserData(userId, user);
    }

    @Override
    public void setLocale(Long userId, String locale) {
        Map<String, String> user = getUserData(userId);
        user.put(BasicCache.LOCALE.toString(), locale);
        updateUserData(userId, user);
        AppUser appUser = appUserDAO.findById(userId);
        appUserDAO.save(appUser.toBuilder().locale(appUserDAO.findLang(locale)).build());
    }

    @Override
    public void removeUserData(Long userId, Long chatId, String locale) {
        userMap.remove(userId);
        sender.sendMessage(SendMessage.builder()
                .text(replyDAO.getReply(BasicState.STOP.toString(), locale).getMessage())
                .chatId(String.valueOf(chatId)).build());
    }

    @Override
    public String getBotState(Long userId) {
        String state = getUserData(userId).get(BasicCache.STATE.toString());
        if (Objects.isNull(state))
            throw new NotFound("Corresponding state not found");

        return state;
    }

    @Override
    public String getLocale(Long userId) {
        return getUserData(userId).get(BasicCache.LOCALE.toString());
    }

    @Override
    public String getMainState(Long userId) {
        String mainState = getUserData(userId).get(BasicCache.MAIN_STATE.toString());
        if (Objects.isNull(mainState))
            throw new NotFound("Corresponding main state not found");

        return mainState;
    }

    @Override
    public boolean existsById(Long userId) {
        return Objects.nonNull(userMap.get(userId));
    }

    @Override
    public Map<String, String> getUserData(Long userId) {
        Map<String, String> user = userMap.get(userId);
        if (Objects.isNull(user))
            throw new NotFound("User not found");

        return user;
    }

    @Override
    public void updateUserData(Long userId, Map<String, String> userData) {
        userMap.put(userId, userData);
    }

    @Override
    public void createUserData(Long userId, Long chatId) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(BasicCache.STATE.toString(), BasicState.START.toString());
        dataMap.put(BasicCache.MAIN_STATE.toString(), BasicState.START.toString());
        dataMap.put(BasicCache.LOCALE.toString(), config.getLang());
        if (appUserDAO.existsById(userId)) {
            AppUser user = appUserDAO.findById(userId);
            dataMap.put(BasicCache.LOCALE.toString(), user.getLocale().getLang());
            appUserDAO.save(user.toBuilder().uuid(UUID.randomUUID().toString()).build());
        } else {
            appUserDAO.save(AppUser.builder().uuid(UUID.randomUUID().toString()).chatId(chatId).locale(appUserDAO.findLang(config.getLang())).userId(userId).build());
        }
        userMap.put(userId, dataMap);
    }
}
