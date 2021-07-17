package az.code.tourapp.repos;

import az.code.tourapp.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfferRepo extends JpaRepository<Offer, Long> , JpaSpecificationExecutor<Offer> {
    boolean existsByUUID(String UUID);
}
