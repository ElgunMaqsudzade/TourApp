package az.code.tourapp.repos;

import az.code.tourapp.models.Locale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocaleRepo extends JpaRepository<Locale, Long> {
    Optional<Locale> findByLang(String lang);
}
