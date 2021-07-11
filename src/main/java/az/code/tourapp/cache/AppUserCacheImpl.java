package az.code.tourapp.cache;


import az.code.tourapp.exceptions.NotFound;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

/**
 * Redis based cache.
 */

@Repository
public class AppUserCacheImpl implements AppUserCache {
    RedisTemplate<String, Map<String, String>> template;

    HashOperations<String, Long, Map<String, String>> hashOperations;
    private final String HASH_KEY = "telegramdatas";

    public AppUserCacheImpl(RedisTemplate<String, Map<String, String>> template) {
        this.template = template;
        this.hashOperations = template.opsForHash();
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
    public Map<String, String> findById(Long userId) {
        Map<String, String> user = hashOperations.get(HASH_KEY, userId);
        if (Objects.isNull(user))
            throw new NotFound("User not found");

        return user;
    }

    @Override
    public void save(Long userId, Map<String, String> userData) {
        hashOperations.put(HASH_KEY, userId, userData);
    }
}
