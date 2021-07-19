package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.DictionaryDAO;
import az.code.tourapp.models.Locale;
import az.code.tourapp.repos.LocaleRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class DictionaryDAOImplTest {

    @Autowired
    private DictionaryDAO dictionaryDAO;
    @Autowired
    private LocaleRepo localeRepo;
    @Test
    void getData() {
        //given
        Class clazz = Locale.class;
        Locale locale = Locale.builder().build();
        localeRepo.save(locale);
        //when
        List data = dictionaryDAO.getData(clazz);
        //expected
        assertThat(locale).isIn(data);
    }
}