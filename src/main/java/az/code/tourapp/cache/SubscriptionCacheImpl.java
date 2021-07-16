package az.code.tourapp.cache;


import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.UserDataDTO;
import az.code.tourapp.exceptions.NotFound;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Objects;

/**
 * Redis based cache.
 */

@Repository
public class SubscriptionCacheImpl implements SubscriptionCache {
    HashOperations<String, Long, UserDataDTO> hashOperations;
    private final String HASH_KEY;

    public SubscriptionCacheImpl(RedisTemplate<String, Object> template, BotConfig config) {
        this.hashOperations = template.opsForHash();
        this.HASH_KEY = config.getRedis().getSubscription();
    }

    @Override
    public void delete(Long userId) {
        hashOperations.delete(HASH_KEY, userId);
    }


    @Override
    public boolean existsById(Long userId) {
        return hashOperations.hasKey(HASH_KEY, userId);
    }

    @Override
    public UserDataDTO findById(Long userId) {
        UserDataDTO user = hashOperations.get(HASH_KEY, userId);
        if (Objects.isNull(user))
            throw new NotFound("User not found");

        return user;
    }

    @Override
    public void save(Long userId, UserDataDTO userData) {
        hashOperations.put(HASH_KEY, userId, userData);
    }
}
