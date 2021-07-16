package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.DictionaryDAO;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class  DictionaryDAOImpl<E> implements DictionaryDAO<E>  {
    @PersistenceContext
    EntityManager em;

    @Override
    public List<E> getData(Class<E> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(clazz);
        Root<E> root = cq.from(clazz);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }
}
