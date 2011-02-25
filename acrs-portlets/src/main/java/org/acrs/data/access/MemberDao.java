package org.acrs.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import org.acrs.data.model.Member;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:28:11 PM
 */
public interface MemberDao extends Dao<Member> {
    /**
     * Returns a member with the given id
     *
     * @param id of the member
     * @return a member
     */
    Member getById(Long id);
    Member getByEmail(String email);
}
