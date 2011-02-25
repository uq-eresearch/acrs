package org.acrs.data.access.jpa;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.acrs.data.access.MemberDao;
import org.acrs.data.model.Member;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:29:42 PM
 */
public class MemberDaoImpl extends JpaDao<Member> implements MemberDao, Serializable {

    public MemberDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public List<Member> getAll() {
        return entityManagerSource.getEntityManager().createQuery("SELECT o FROM Member o ORDER BY o.firstName").getResultList();
    }

    @Override
    public Member getById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Member o WHERE o.id = :id");
        query.setParameter("id", id);
        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "Email should be unique";
        return (Member) resultList.get(0);
    }
    
    public Member getByEmail(String email) {
    	Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Member o WHERE o.email = :email");
    	query.setParameter("email", email);
    	List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        assert resultList.size() == 1 : "Email should be unique";
        return (Member) resultList.get(0);
    }
}
