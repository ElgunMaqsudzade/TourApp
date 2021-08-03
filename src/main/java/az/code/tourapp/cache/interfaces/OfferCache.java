package az.code.tourapp.cache.interfaces;

import java.util.Map;

public interface OfferCache {

    boolean existsById(String uuid);

    void delete(String uuid);

    Map<Integer, Long> findById(String uuid);

    Long findById(String uuid, Integer messageId);

    void save(String uuid, Map<Integer, Long> map);

    void add(String uuid, Integer messageId, Long offerId);
}
