package az.code.tourapp.daos.interfaces;

import java.util.List;

public interface DictionaryDAO<E> {
    List<E> getData(Class<E> clazz);

}
