package az.code.tourapp.cache.interfaces;

import az.code.tourapp.dtos.OfferCacheDTO;

import java.util.Set;

public interface OfferCache {
    boolean existsById(String UUID);

    void delete(String UUID);

    OfferCacheDTO findById(String UUID);

    void save(String UUID, OfferCacheDTO userData);

    void create(String UUID);

    OfferCacheDTO increase(String UUID);

    OfferCacheDTO setLocked(boolean value, String uuid);

    Set<String> getUUIDList();
}
