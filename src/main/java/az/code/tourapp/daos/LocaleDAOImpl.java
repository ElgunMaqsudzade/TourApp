package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.LocaleDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.Locale;
import az.code.tourapp.repos.LocaleRepo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LocaleDAOImpl implements LocaleDAO {
    LocaleRepo localeRepo;

    public LocaleDAOImpl(LocaleRepo localeRepo) {
        this.localeRepo = localeRepo;
    }

    @Override
    public Locale findByLang(String lang) {
        Optional<Locale> locale = localeRepo.findByLang(lang);
        if (locale.isEmpty()) throw new NotFound("Language couldn't found");
        return locale.get();
    }
}
