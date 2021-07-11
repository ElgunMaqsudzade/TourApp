package az.code.tourapp.repos;

import az.code.tourapp.models.Locale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LocaleRepo extends JpaRepository<Locale, Long> {
    Optional<Locale> findByLang(String lang);

    @Query("from Locale where lower(lang) LIKE lower(concat('%', :code,'%')) ")
    Optional<Locale> findByLangCode(String code);
}
