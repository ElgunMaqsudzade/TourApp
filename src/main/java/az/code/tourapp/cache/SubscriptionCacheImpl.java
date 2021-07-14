package az.code.tourapp.cache;


import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.exceptions.NotFound;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

/**
 * Redis based cache.
 */

@Repository
@ConfigurationProperties(prefix = "telegrambot.hashkey", ignoreInvalidFields = true)
public class SubscriptionCacheImpl implements SubscriptionCache {
    private final String SUB_HASH_KEY = "telegramsubdata";
    private final String ACTION_HASH_KEY = "telegramactiondata";

    RedisTemplate<String, Map<String, String>> subData;
    RedisTemplate<String, Map<String, String>> actionData;

    HashOperations<String, Long, Map<String, String>> hashOperations;

    public SubscriptionCacheImpl(RedisTemplate<String, Map<String, String>> template) {
        this.subData = template;
        this.actionData = template;
        this.hashOperations = template.opsForHash();
    }

    @Override
    public void delete(Long userId) {
        hashOperations.delete(SUB_HASH_KEY, userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return hashOperations.hasKey(SUB_HASH_KEY, userId);
    }

    @Override
    public Map<String, String> findById(Long userId) {
        Map<String, String> user = hashOperations.get(SUB_HASH_KEY, userId);
        if (Objects.isNull(user))
            throw new NotFound("User not found");

        return user;
    }

    @Override
    public void saveSub(Long userId, Map<String, String> userData) {
        hashOperations.put(SUB_HASH_KEY, userId, userData);
    }

    @Override
    public void saveAction(Long userId, Map<String, String> action) {
        hashOperations.put(ACTION_HASH_KEY, userId, action);
    }
}
