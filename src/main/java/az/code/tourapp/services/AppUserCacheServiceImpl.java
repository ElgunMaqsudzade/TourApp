package az.code.tourapp.services;

import az.code.tourapp.cache.AppUserCache;
import az.code.tourapp.components.MessageSender;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.models.enums.BasicCache;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.models.enums.InputType;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.BotState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class AppUserCacheServiceImpl implements AppUserCacheService {
    AppUserDAO appUserDAO;
    AppUserCache cache;
    MessageSender sender;
    ReplyDAO replyDAO;

    public AppUserCacheServiceImpl(AppUserDAO appUserDAO, AppUserCache cache, MessageSender sender, ReplyDAO replyDAO) {
        this.appUserDAO = appUserDAO;
        this.cache = cache;
        this.sender = sender;
        this.replyDAO = replyDAO;
    }

    @Override
    public boolean saveData(Long userId, String botState, String data) {
        BotState state = appUserDAO.getState(botState);
        Map<String, String> user = findById(userId);

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
        update(userId, user);
        return true;
    }

    @Override
    public void setState(Long userId, String state) {
        Map<String, String> user = findById(userId);
        user.put(BasicCache.STATE.toString(), state);
        update(userId, user);
    }

    @Override
    public void setMainState(Long userId, BasicState mainState) {
        Map<String, String> user = findById(userId);
        user.put(BasicCache.MAIN_STATE.toString(), mainState.toString());
        update(userId, user);
    }

    @Override
    public void setLocale(Long userId, String locale) {
        Map<String, String> user = findById(userId);
        user.put(BasicCache.LOCALE.toString(), locale);
        update(userId, user);
        AppUser appUser = appUserDAO.findById(userId);
        appUserDAO.save(appUser.toBuilder().locale(appUserDAO.findLang(locale)).build());
    }

    @Override
    public String getBotState(Long userId) {
        String state = findById(userId).get(BasicCache.STATE.toString());
        if (Objects.isNull(state))
            throw new NotFound("Corresponding state not found");

        return state;
    }

    @Override
    public String getLocale(Long userId) {
        return findById(userId).get(BasicCache.LOCALE.toString());
    }

    @Override
    public String getMainState(Long userId) {
        String mainState = findById(userId).get(BasicCache.MAIN_STATE.toString());
        if (Objects.isNull(mainState))
            throw new NotFound("Corresponding main state not found");

        return mainState;
    }

    @Override
    public boolean existsById(Long userId) {
        return cache.existsById(userId);
    }

    @Override
    public void delete(Long userId, Long chatId, String locale) {
        cache.delete(userId);
        sender.sendMessage(SendMessage.builder()
                .text(replyDAO.getReply(BasicState.STOP.toString(), locale).getMessage())
                .chatId(String.valueOf(chatId)).build());
    }

    @Override
    public Map<String, String> findById(Long userId) {
        return cache.findById(userId);
    }

    @Override
    public void update(Long userId, Map<String, String> userData) {
        cache.save(userId, userData);
    }

    @Override
    public void create(Long userId, Long chatId) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(BasicCache.STATE.toString(), BasicState.START.toString());
        dataMap.put(BasicCache.MAIN_STATE.toString(), BasicState.START.toString());
        if (appUserDAO.existsById(userId)) {
            AppUser user = appUserDAO.findById(userId);
            dataMap.put(BasicCache.LOCALE.toString(), user.getLocale().getLang());
            appUserDAO.save(user.toBuilder().uuid(UUID.randomUUID().toString()).build());
        } else {
            appUserDAO.save(AppUser.builder().uuid(UUID.randomUUID().toString()).chatId(chatId).userId(userId).build());
        }
        update(userId, dataMap);
    }
}
