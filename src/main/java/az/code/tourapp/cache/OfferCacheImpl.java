package az.code.tourapp.cache;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.OfferCacheDTO;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OfferCacheImpl implements OfferCache {
    HashOperations<String, String, OfferCacheDTO> hashOps;

    private final String HASH_KEY;

    public OfferCacheImpl(RedisTemplate<String, Object> template, BotConfig config) {
        this.hashOps = template.opsForHash();
        this.HASH_KEY = config.getRedis().getOffer();
    }

    @Override
    public boolean existsById(String UUID) {
        return hashOps.hasKey(HASH_KEY, UUID);
    }

    @Override
    public void delete(String UUID) {
        hashOps.delete(HASH_KEY, UUID);
    }

    @Override
    public OfferCacheDTO findById(String UUID) {
        return hashOps.get(HASH_KEY, UUID);
    }

    @Override
    public void save(String UUID, OfferCacheDTO userData) {
        hashOps.put(HASH_KEY, UUID, userData);
    }

    @Override
    public void create(String UUID) {
        if (!existsById(UUID))
            hashOps.put(HASH_KEY, UUID, OfferCacheDTO.builder().build());
    }

    @Override
    public OfferCacheDTO increase(String UUID) {
        OfferCacheDTO cacheDTO = findById(UUID);
        cacheDTO.setOfferCount(cacheDTO.getOfferCount() + 1);
        save(UUID, cacheDTO);
        return cacheDTO;
    }
}
