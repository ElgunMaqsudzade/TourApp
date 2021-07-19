package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.OfferDAO;
import az.code.tourapp.models.Offer;
import az.code.tourapp.models.Offer_;
import az.code.tourapp.repos.OfferRepo;
import az.code.tourapp.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OfferDAOImpl implements OfferDAO {
    private final OfferRepo offerRepo;

    @Override
    public boolean existsByUUID(String uuid) {
        return offerRepo.existsByUUID(uuid);
    }

    @Override
    public Offer save(Offer offer) {
        return offerRepo.save(offer);
    }

    @Override
    public void deleteAll(List<Offer> offerList) {
        offerRepo.deleteAll(offerList);
    }

    @Override
    public void delete(Offer offer) {
        offerRepo.delete(offer);
    }

    @Override
    public Optional<Offer> findFirstByUUID(String uuid) {
        Specification<Offer> offerSpecification = (root, query, cb) -> cb.equal(root.get(Offer_.U_UI_D), uuid);
        Page<Offer> offerPage = offerRepo
                .findAll(offerSpecification, PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id")));
        return offerPage.getContent().stream().findFirst();
    }
}
