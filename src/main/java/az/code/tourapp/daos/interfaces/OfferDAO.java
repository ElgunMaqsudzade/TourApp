package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.Offer;

import java.util.List;
import java.util.Optional;

public interface OfferDAO {
    boolean existsByUUID(String uuid);

    Offer save(Offer offer);

    void deleteAll(List<Offer> offerList);

    void delete(Offer offer);

    Optional<Offer> findFirstByUUID(String uuid);
}
