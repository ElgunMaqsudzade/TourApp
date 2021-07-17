package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.Offer;
import lombok.SneakyThrows;

import java.util.Optional;

public interface OfferDAO {
    Optional<Offer> pop(String uuid);

    boolean exists(String uuid);

    Offer save(Offer offer);

    void deleteByUUID(String uuid);
}
