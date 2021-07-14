package az.code.tourapp.cache.interfaces;



import java.util.Map;


public interface SubscriptionCache {

    boolean existsById(Long userId);

    void delete(Long userId);

    Map<String, String> findById(Long userId);

    void saveSub(Long userId, Map<String, String> userData);

    void saveAction(Long userId, Map<String, String> userData);
}
