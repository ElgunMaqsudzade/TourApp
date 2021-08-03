package az.code.tourapp.cache;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.DictionaryDTO;
import az.code.tourapp.dtos.UserDataDTO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Error;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;
import az.code.tourapp.models.enums.BasicCache;
import az.code.tourapp.models.enums.BasicState;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class DictionaryCacheImpl implements DictionaryCache {
    SubscriptionCache cache;

    HashOperations<String, String, DictionaryDTO> hashOps;

    private final String HASH_KEY;

    public DictionaryCacheImpl(SubscriptionCache cacheService, RedisTemplate<String, Object> template, BotConfig config) {
        this.hashOps = template.opsForHash();
        this.HASH_KEY = config.getRedis().getDictionary();
        this.cache = cacheService;
    }

    @Override
    public Reply getReply(Long userId) {
        UserDataDTO user = cache.findById(userId);
        String lang = user.getLang().getLang();
        String state = user.getState().getState();
        return getReply(state, lang);
    }

    @Override
    public Reply getReply(String state, String lang) {
        Optional<Reply> reply = Optional.empty();
        if (hashOps.hasKey(HASH_KEY, HASH_KEY)) {
            List<Reply> replyList = Objects.requireNonNull(hashOps.get(HASH_KEY, HASH_KEY)).getReplyList();
            reply = replyList.stream().parallel().filter(i -> i.getState().getState().equals(state) && i.getLocale().getLang().equals(lang)).findFirst();
        }
        if (reply.isEmpty()) throw new NotFound("Reply not found in cache");
        return reply.get();
    }

    @Override
    public Error getError(String state, String lang) {
        Optional<Error> error = Optional.empty();
        List<Error> errors = Objects.requireNonNull(hashOps.get(HASH_KEY, HASH_KEY)).getErrors();
        if (hashOps.hasKey(HASH_KEY, HASH_KEY)) {
            error = errors.parallelStream().filter(i -> i.getState().getState().equals(state) && i.getLocale().getLang().equals(lang)).findFirst();
        }
        if (error.isEmpty()){
            error = errors.parallelStream()
                    .filter(i -> i.getState().getState().equals(BasicState.ERROR.toString()) && i.getLocale().getLang().equals(lang))
                    .findFirst();
        };
        return error.get();
    }

    @Override
    public BotState getState(String state) {
        Optional<BotState> botState = Optional.empty();
        if (hashOps.hasKey(HASH_KEY, HASH_KEY)) {
            List<BotState> states = Objects.requireNonNull(hashOps.get(HASH_KEY, HASH_KEY)).getBotStates();
            botState = states.stream().parallel().filter(i -> i.getState().equals(state)).findFirst();
        }
        if (botState.isEmpty()) throw new NotFound("BotState not found in cache");
        return botState.get();
    }

    @Override
    public Locale getLocale(String lang) {
        Optional<Locale> locale = Optional.empty();
        if (hashOps.hasKey(HASH_KEY, HASH_KEY)) {
            List<Locale> states = Objects.requireNonNull(hashOps.get(HASH_KEY, HASH_KEY)).getLocales();
            locale = states.stream().parallel().filter(i -> i.getLang().equals(lang)).findFirst();
        }
        if (locale.isEmpty()) throw new NotFound("Locale not found in cache");
        return locale.get();
    }
}
