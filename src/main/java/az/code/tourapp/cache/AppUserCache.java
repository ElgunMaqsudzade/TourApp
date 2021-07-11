package az.code.tourapp.cache;



import java.util.Map;


public interface AppUserCache {

    boolean existsById(Long userId);

    void delete(Long userId);

    Map<String, String> findById(Long userId);

    void save(Long userId, Map<String, String> userData);
}
