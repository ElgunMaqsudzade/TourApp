package az.code.tourapp.repos;

import az.code.tourapp.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepo extends JpaRepository<Offer,Long> {
}
