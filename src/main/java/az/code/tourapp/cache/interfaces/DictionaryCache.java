package az.code.tourapp.cache.interfaces;

import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;

public interface DictionaryCache {
    Reply getReply(Long userId);

    Reply getReply(String state, String lang);

    BotState getState(String state);

    Locale getLocale(String lang);
}
