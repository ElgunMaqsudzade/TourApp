package az.code.tourapp.cache;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.exceptions.NotFound;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OfferCacheImpl implements OfferCache {
    HashOperations<String, String, Map<Integer, Long>> hashOps;

    private final String HASH_KEY;

    public OfferCacheImpl(RedisTemplate<String, Object> template, BotConfig config) {
        this.hashOps = template.opsForHash();
        this.HASH_KEY = config.getRedis().getOffer();
    }

    @Override
    public boolean existsById(String uuid) {
        return hashOps.hasKey(HASH_KEY, uuid);
    }

    @Override
    public void delete(String uuid) {
        hashOps.delete(HASH_KEY, uuid);
    }

    @Override
    public Map<Integer, Long> findById(String uuid) {
        if (!hashOps.hasKey(HASH_KEY, uuid)) {
            throw new NotFound("Offers not found");
        }
        return hashOps.get(HASH_KEY, uuid);
    }

    @Override
    public Long findById(String uuid, Integer messageId) {
        Map<Integer, Long> map = findById(uuid);
        if (!map.containsKey(messageId)) {
            throw new NotFound("OfferId not found");
        }
        return map.get(messageId);
    }

    @Override
    public void save(String uuid, Map<Integer, Long> map) {
        hashOps.put(HASH_KEY, uuid, map);
    }

    @Override
    public void add(String uuid, Integer messageId, Long offerId) {
        Map<Integer, Long> map = new HashMap<>();
        if (existsById(uuid)) {
            map = findById(uuid);
        }
        map.put(messageId, offerId);
        save(uuid, map);
    }

}
