package az.code.tourapp.cache;

import az.code.tourapp.dtos.BotState;
import az.code.tourapp.dtos.AppUserDTO;


public interface AppUserCache {

    void setAppUserBotState(Long userId, BotState botState);

    BotState getAppUserBotState(Long userId);

    boolean existsById(Long userId);

    AppUserDTO getAppUserData(Long userId);

    void saveAppUserData(Long userId, AppUserDTO userProfileData);
}
