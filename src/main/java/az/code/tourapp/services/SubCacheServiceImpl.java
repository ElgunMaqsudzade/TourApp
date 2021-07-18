package az.code.tourapp.services;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.components.WebhookBotComponent;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.UserDataDTO;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.enums.BasicCache;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.enums.InputType;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.SubCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubCacheServiceImpl implements SubCacheService {
    private final SubscriptionCache cache;
    private final DictionaryCache dCache;
    private final AppUserDAO appUserDAO;
    private final WebhookBotComponent sender;


    @Override
    public boolean saveData(Long userId, String botState, String data) {
        BotState state = appUserDAO.getState(botState);
        UserDataDTO user = findById(userId);
        String regex = state.getRegex();

        if (botState.equals(BasicCache.language.toString())) {
            setLocale(userId, data);
            return true;
        }
        if (regex == null) {
            user.getSubscription().put(state.getState(), data);
        } else {
            if (regex.equals(InputType.DATE.toString())) {
                try {
                    LocalDate.parse(data);
                    user.getSubscription().put(state.getState(), data);
                } catch (Exception ex) {
                    return false;
                }
            } else if (data.matches(regex)) {
                user.getSubscription().put(state.getState(), data);
            } else {
                return false;
            }
        }
        update(userId, user);
        return true;
    }

    @Override
    public void setState(Long userId, String state) {
        UserDataDTO userDataDTO = findById(userId);
        update(userId, userDataDTO.toBuilder().state(dCache.getState(state)).build());
    }

    @Override
    public void setMainState(Long userId, BasicState mainState) {
        UserDataDTO userDataDTO = findById(userId);
        update(userId, userDataDTO.toBuilder().mainState(mainState).build());
    }

    @Override
    public void setLocale(Long userId, String locale) {
        UserDataDTO userData = findById(userId);
        userData.getSubscription().put(BasicCache.language.toString(), locale);
        userData.setLang(dCache.getLocale(locale));
        update(userId, userData);
    }

    @Override
    public BotState getBotState(Long userId) {
        BotState state = findById(userId).getState();
        if (Objects.isNull(state))
            throw new NotFound("Corresponding state not found");

        return state;
    }

    @Override
    public String getLocale(Long userId) {
        return findById(userId).getLang().getLang();
    }

    @Override
    public String getMainState(Long userId) {
        BasicState mainState = findById(userId).getMainState();
        if (Objects.isNull(mainState))
            throw new NotFound("Corresponding main state not found");

        return mainState.toString();
    }

    @Override
    public boolean existsById(Long userId) {
        return cache.existsById(userId);
    }

    @Override
    public boolean existsSubById(Long userId) {
        return !findById(userId).getSubscription().isEmpty();
    }

    @Override
    public void deleteSubscription(Long userId) {
        UserDataDTO user = findById(userId);
        update(userId, user.toBuilder().subscription(new HashMap<>()).build());
    }

    @Override
    public void delete(Long userId, Long chatId) {
        sender.sendMessage(SendMessage.builder().chatId(String.valueOf(chatId))
                .text(dCache.getReply(userId).getMessage()).build());
        cache.delete(userId);
    }

    @Override
    public UserDataDTO findById(Long userId) {
        return cache.findById(userId);
    }

    @Override
    public void update(Long userId, UserDataDTO userData) {
        cache.save(userId, userData);
    }

    @Override
    public void create(Long userId, Long chatId) {
        String key = UUID.randomUUID().toString();
        UserDataDTO user = UserDataDTO.builder()
                .mainState(BasicState.START)
                .state(dCache.getState(BasicState.START.toString()))
                .lang(dCache.getLocale("English"))
                .subscription(new HashMap<>())
                .build();
        user.getSubscription().put(BasicCache.language.toString(), "English");
        user.getSubscription().put("telegramIdentifier", key);
        appUserDAO.save(AppUser.builder().uuid(key).chatId(chatId).userId(userId).build());
        update(userId, user);
    }
}
