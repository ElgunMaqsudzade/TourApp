package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.Locale;

public interface LocaleDAO {
    Locale findByLang(String lang);
}
