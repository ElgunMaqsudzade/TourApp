package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.OfferDAO;
import az.code.tourapp.models.Offer;
import az.code.tourapp.repos.OfferRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class OfferDAOImplTest {
    @Autowired
    private OfferDAO offerDAO;
    @Autowired
    private OfferRepo offerRepo;

    @Test
    void existsByUUID() {
        //given
        String uuid = UUID.randomUUID().toString();
        Offer offer = Offer.builder().UUID(uuid).build();
        offerRepo.save(offer);
        //when
        boolean exists = offerRepo.existsByUUID(uuid);
        //expected
        assertThat(exists).isTrue();
    }

    @Test
    void save() {
        //given
        Offer offer = Offer.builder().build();
        long size = offerRepo.count();
        //when
        offerRepo.save(offer);
        //expected
        long newSize = offerRepo.count();
        assertThat(newSize).isEqualTo(size + 1);
    }

    @Test
    void deleteAll() {
        //given
        int count = 10;
        List<Offer> offerList = IntStream.range(0, count).mapToObj(i -> new Offer()).collect(Collectors.toList());
        offerRepo.saveAll(offerList);
        long size = offerRepo.count();
        //when
        offerDAO.deleteAll(offerList);
        //expected
        long newSize = offerRepo.count();
        assertThat(newSize).isEqualTo(size - count);
    }

    @Test
    void delete() {
        //given
        Offer offer = new Offer();
        offerRepo.save(offer);
        long size = offerRepo.count();
        //when
        offerDAO.delete(offer);
        //expected
        long newSize = offerRepo.count();
        assertThat(newSize).isEqualTo(size - 1);
    }

    @Test
    void findFirstByUUID() {
        //given
        String uuid = UUID.randomUUID().toString();
        Offer offer = Offer.builder().UUID(uuid).build();
        offerRepo.save(offer);
        //when
        Optional<Offer> firstByUUID = offerDAO.findFirstByUUID(uuid);
        //expected
        assertThat(firstByUUID).isEqualTo(Optional.of(offer));
    }
}