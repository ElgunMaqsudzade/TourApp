package az.code.tourapp.services;

import az.code.tourapp.daos.interfaces.OfferDAO;
import az.code.tourapp.models.Offer;
import az.code.tourapp.models.Offer_;
import az.code.tourapp.repos.OfferRepo;
import az.code.tourapp.services.interfaces.OfferService;
import az.code.tourapp.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    private final ImageUtil imageUtil;
    private final OfferDAO offerDAO;
    private final OfferRepo offerRepo;

    @SneakyThrows
    @Override
    public Optional<Offer> pop(String uuid) {
        Optional<Offer> opOffer = offerDAO.findFirstByUUID(uuid);
        if (opOffer.isPresent()) {
            Offer offer = opOffer.get();
            offer.setFileAsBytes(Files.readAllBytes(Paths.get(offer.getFilePath())));
            imageUtil.deleteImage(offer.getFilePath());
            offerDAO.delete(offer);
        }
        return opOffer;
    }

    @Override
    public boolean existsByUUID(String uuid) {
        return offerDAO.existsByUUID(uuid);
    }

    @Override
    public Offer save(Offer offer) {
        String pathName = imageUtil.saveImage(offer.getFileAsBytes());
        return offerDAO.save(offer.toBuilder().filePath(pathName).build());
    }

    @Override
    public void deleteByUUID(String uuid) {
        Specification<Offer> offerSpecification = (root, query, cb) -> cb.equal(root.get(Offer_.U_UI_D), uuid);
        List<Offer> offerList = offerRepo.findAll(offerSpecification);
        if (offerList.size() > 0) {
            offerList.stream().parallel().forEach(i -> imageUtil.deleteImage(i.getFilePath()));
        }
        offerDAO.deleteAll(offerList);
    }
}
