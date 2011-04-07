package org.acrs.data.access;

import org.acrs.data.model.ConferenceRegistration;

import au.edu.uq.itee.maenad.dataaccess.Dao;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:28:11 PM
 */
public interface ConferenceRegistrationDao extends Dao<ConferenceRegistration> {
    /**
     * Returns a Conference Registration with the given id
     *
     * @param id of the conferenceRegistration
     * @return a conference registration
     */
	ConferenceRegistration getById(Long id);
	ConferenceRegistration getByEmail(String email);
}
