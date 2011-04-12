package org.acrs.portlets;

import java.util.Enumeration;
import java.util.HashMap;

import javax.portlet.ActionRequest;

import org.acrs.data.model.ConferenceRegistration;
import org.apache.commons.beanutils.BeanUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * A very simple class containing all the registration form fields, stored as
 * strings.
 * 
 * Is used to populate the form fields for editing, and to return user values to
 * them if they make an error filling in the form.
 * 
 * @author uqdayers
 */
public class ConferenceFormBean {
	private static Log _log = LogFactoryUtil
			.getLog(ConferenceFormBean.class);
	private String title = "";
	private String firstName = "";
	private String lastName = "";
	private String streetAddress = "";
	private String streetAddress2 = "";
	private String city = "";
	private String state = "";
	private String postcode = "";
	private String country = "";
	private String email = "";
	private String phone = "";
	private String institution = "";
	private String submittingAbstract = "";
	private String registrationRate = "";
	private String studentMentoringDay = "";
	private String coralFinderWorkshop = "";
	private String additionalTicketsWelcome = "";
	private String additionalTicketsDinner = "";

	public ConferenceFormBean(ConferenceRegistration registration) {
		try {
			BeanUtils.copyProperties(this, registration);
		} catch (Exception e) {
			_log.error("Error copying properties", e);
//			throw new RegistrationProcessingException("Error creating FormBean from existing registration", e);
		}
	}
	
	public ConferenceFormBean(ActionRequest actionRequest) {
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		Enumeration<String> names = actionRequest.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, actionRequest.getParameterValues(name));
		}
		try {
			BeanUtils.populate(this, map);
		} catch (Exception e) {
//			throw new RegistrationProcessingException("Error creating FormBean from actionRequest", e);
		}

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getStreetAddress2() {
		return streetAddress2;
	}

	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getSubmittingAbstract() {
		return submittingAbstract;
	}

	public void setSubmittingAbstract(String submittingAbstract) {
		this.submittingAbstract = submittingAbstract;
	}

	public String getRegistrationRate() {
		return registrationRate;
	}

	public void setRegistrationRate(String registrationRate) {
		this.registrationRate = registrationRate;
	}

	public String getCoralFinderWorkshop() {
		return coralFinderWorkshop;
	}

	public void setCoralFinderWorkshop(String coralFinderWorkshop) {
		this.coralFinderWorkshop = coralFinderWorkshop;
	}

	public String getAdditionalTicketsWelcome() {
		return additionalTicketsWelcome;
	}

	public void setAdditionalTicketsWelcome(String additionalTicketsWelcome) {
		this.additionalTicketsWelcome = additionalTicketsWelcome;
	}

	public String getAdditionalTicketsDinner() {
		return additionalTicketsDinner;
	}

	public void setAdditionalTicketsDinner(String additionalTicketsDinner) {
		this.additionalTicketsDinner = additionalTicketsDinner;
	}

	public void setStudentMentoringDay(String studentMentoringDay) {
		this.studentMentoringDay = studentMentoringDay;
	}

	public String getStudentMentoringDay() {
		return studentMentoringDay;
	}

}