package az.code.tourapp.services.interfaces;

import az.code.tourapp.models.Offer;

import java.util.Optional;

public interface OfferService {

    Optional<Offer> pop(String uuid);

    boolean existsByUUID(String uuid);

    Offer save(Offer offer);

    void deleteByUUID(String uuid);
}
