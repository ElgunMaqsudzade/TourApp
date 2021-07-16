package az.code.tourapp.cache.interfaces;


import az.code.tourapp.dtos.UserDataDTO;


public interface SubscriptionCache {

    boolean existsById(Long userId);

    void delete(Long userId);

    UserDataDTO findById(Long userId);

    void save(Long userId, UserDataDTO userData);
}
