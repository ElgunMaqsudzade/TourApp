package az.code.tourapp.cache;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.services.interfaces.OfferService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OfferCacheImpl implements OfferCache {
    OfferService offerService;
    SchedulerExecutor sch;
    HashOperations<String, String, OfferCacheDTO> hashOps;

    private final String HASH_KEY;

    private final Integer offerCount;

    public OfferCacheImpl(SchedulerExecutor sch, OfferService offerService, RedisTemplate<String, Object> template, BotConfig config) {
        this.offerService = offerService;
        this.hashOps = template.opsForHash();
        this.HASH_KEY = config.getRedis().getOffer();
        this.sch = sch;
        this.offerCount = config.getOffer().getCount();
    }

    @Override
    public boolean existsById(String UUID) {
        return hashOps.hasKey(HASH_KEY, UUID);
    }

    @Override
    public void delete(String UUID) {
        offerService.deleteByUUID(UUID);
        hashOps.delete(HASH_KEY, UUID);
    }

    @Override
    public OfferCacheDTO findById(String UUID) {
        if (!hashOps.hasKey(HASH_KEY, UUID)) {
            throw new NotFound("Offer not found");
        }
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
        if (cacheDTO.getOfferCount() % offerCount == 0) {
            cacheDTO.setLocked(true);
        }
        save(UUID, cacheDTO);
        return cacheDTO;
    }

    @Override
    public OfferCacheDTO setLocked(boolean value, String uuid) {
        OfferCacheDTO cacheDTO = findById(uuid);
        cacheDTO.setLocked(value);
        save(uuid, cacheDTO);
        if (!value) {
            sch.runDBOffersJob(uuid);
        }
        return cacheDTO;
    }


    @Override
    public Set<String> getUUIDList() {
        return hashOps.keys(HASH_KEY);
    }
}
