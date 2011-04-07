package org.acrs.data.access.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.acrs.data.access.ConferenceRegistrationDao;
import org.acrs.data.model.ConferenceRegistration;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:29:42 PM
 */
public class ConferenceRegistrationDaoImpl extends JpaDao<ConferenceRegistration> implements ConferenceRegistrationDao, Serializable {

    public ConferenceRegistrationDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public List<ConferenceRegistration> getAll() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM ConferenceRegistration o WHERE isActive = true ORDER BY o.lastName").getResultList();
    }

    @Override
    public ConferenceRegistration getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM ConferenceRegistration o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "Email should be unique";
        return (ConferenceRegistration) resultList.get(0);
    }
    
    public ConferenceRegistration getByEmail(String email) {
    	Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM ConferenceRegistration o WHERE o.email = :email");
    	query.setParameter("email", email);
    	List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "Email should be unique";
        return (ConferenceRegistration) resultList.get(0);
    }
}
