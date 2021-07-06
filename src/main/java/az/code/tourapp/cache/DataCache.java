package az.code.tourapp.cache;

import az.code.tourapp.dtos.BotState;
import az.code.tourapp.models.AppUser;


public interface DataCache {

    void setAppUserBotState(Long userId, BotState botState);

    BotState getAppUserBotState(Long userId);

    AppUser getAppUserData(Long userId);

    void saveAppUserData(Long userId, AppUser userProfileData);
}
