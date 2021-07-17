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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OfferDAOImpl implements OfferDAO {
    private final OfferRepo offerRepo;
    private final ImageUtil imageUtil;


    @SneakyThrows
    @Override
    public Optional<Offer> pop(String uuid) {
        Specification<Offer> offerSpecification = (root, query, cb) -> cb.equal(root.get(Offer_.U_UI_D), uuid);
        Page<Offer> offerPage = offerRepo
                .findAll(offerSpecification, PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id")));

        Optional<Offer> opOffer = offerPage.getContent().stream().findFirst();
        if (opOffer.isPresent()) {
            Offer offer = opOffer.get();
            offer.setFileAsBytes(Files.readAllBytes(Paths.get(offer.getFilePath())));
            imageUtil.deleteImage(offer.getFilePath());
            offerRepo.delete(offer);
        }
        return opOffer;
    }

    @Override
    public boolean exists(String uuid) {
        return offerRepo.existsByUUID(uuid);
    }

    @Override
    public Offer save(Offer offer) {
        String pathName = imageUtil.saveImage(offer.getFileAsBytes());
        return offerRepo.save(offer.toBuilder().filePath(pathName).build());
    }

    @Override
    public void deleteByUUID(String uuid) {
        Specification<Offer> offerSpecification = (root, query, cb) -> cb.equal(root.get(Offer_.U_UI_D), uuid);
        List<Offer> offerList = offerRepo.findAll(offerSpecification);
        if (offerList.size() > 0) {
            offerList.stream().parallel().forEach(i -> imageUtil.deleteImage(i.getFilePath()));
        }
        offerRepo.deleteAll(offerList);
    }
}
