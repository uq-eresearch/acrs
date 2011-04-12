package org.acrs.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.acrs.portlets.ConferenceFormBean;
import org.acrs.portlets.RegistrationProcessingException;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.NotNull;

/**
 * 
 * @author Damien Ayers From 04/04/2011
 * 
 *         Author: alabri Date: 08/02/2011 Time: 4:05:37 PM
 */

@Entity
public class ConferenceRegistration {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;
	private String firstName;
	private String lastName;
	private String streetAddress;
	private String streetAddress2;
	private String city;
	private String state;
	private String postcode;
	private String country;
	private String email;
	private String phone;

	@Column(length = 500)
	private String institution;

	private Boolean submittingAbstract;

	private String registrationRate;

	private Boolean studentMentoringDay;
	private Boolean coralFinderWorkshop;
	private Integer additionalTicketsWelcome;
	private Integer additionalTicketsDinner;

	private Integer registrationAmount;

	@Transient
	private String paypalItemName;

	@Column(length = 2500)
	private String paypalRef;
	private String paypalStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date registrationDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	private Boolean isActive;

	public ConferenceRegistration() {
		this.registrationDate = new Date();

	}

	public ConferenceRegistration(ConferenceFormBean cfb)
			throws RegistrationProcessingException {
		this.registrationDate = new Date();
		try {
			BeanUtils.copyProperties(this, cfb);
		} catch (Exception e1) {
			throw new RegistrationProcessingException(
					"Error creating database record" + e1);
		}
	}

	public ConferenceRegistration(String firstName, String lastName,
			String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.registrationDate = new Date();
	}

	public void calculateRegistration() throws RegistrationProcessingException {
		// calculate Registration amount
		paypalItemName = "Australian Coral Reef Society Conference 2011";
		registrationAmount = 0;


		if ("StudentMember".equals(registrationRate)) {
			registrationAmount = 330;
			paypalItemName += " Student Member Registration";
		} else if ("StudentNonMember".equals(registrationRate)) {
			registrationAmount = 380;
			paypalItemName += " Student Non-Member Registration";
		} else if ("FullMember".equals(registrationRate)) {
			registrationAmount = 440;
			paypalItemName += " Full Member Registration";
		} else if ("FullNonMember".equals(registrationRate)) {
			registrationAmount = 499;
			paypalItemName += " Full Non-Member Registration";
		} else if ("DayOneOnly".equals(registrationRate)) {
			registrationAmount = 240;
			paypalItemName += " Day rate — Day 1 only";
		} else if ("DayTwoOnly".equals(registrationRate)) {
			registrationAmount = 240;
			paypalItemName += " Day rate — Day 2 only";
		} else {
			throw new RegistrationProcessingException(
					"Can't calculate registration rate for: "
							+ registrationRate);
		}
		if (this.getStudentMentoringDay()) {
			registrationAmount -= 50;
			paypalItemName += " + Student Mentoring Day";
		}

		if (this.getCoralFinderWorkshop()) {
			registrationAmount += 10;
			paypalItemName += " + Coral Finder Workshop";
		}

		if (this.getAdditionalTicketsWelcome() > 0) {
			registrationAmount += this.getAdditionalTicketsWelcome() * 10;
			paypalItemName += " + " + this.getAdditionalTicketsWelcome()
					+ " Welcome Event Tickets";
		}

		if (this.getAdditionalTicketsDinner() > 0) {
			registrationAmount += this.getAdditionalTicketsDinner() * 10;
			paypalItemName += " + " + this.getAdditionalTicketsDinner()
					+ " Dinner Tickets";
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getPaypalRef() {
		return paypalRef;
	}

	public void setPaypalRef(String paypalRef) {
		this.paypalRef = paypalRef;
	}

	public String getPaypalStatus() {
		return paypalStatus;
	}

	public void setPaypalStatus(String paypalStatus) {
		this.paypalStatus = paypalStatus;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getSubmittingAbstract() {
		return submittingAbstract;
	}

	public void setSubmittingAbstract(Boolean submittingAbstract) {
		this.submittingAbstract = submittingAbstract;
	}

	public String getRegistrationRate() {
		return registrationRate;
	}

	public void setRegistrationRate(String registrationRate) {
		this.registrationRate = registrationRate;
	}

	public Boolean getStudentMentoringDay() {
		return studentMentoringDay;
	}

	public void setStudentMentoringDay(Boolean studentMentoringDay) {
		this.studentMentoringDay = studentMentoringDay;
	}

	public Boolean getCoralFinderWorkshop() {
		return coralFinderWorkshop;
	}

	public void setCoralFinderWorkshop(Boolean coralFinderWorkshop) {
		this.coralFinderWorkshop = coralFinderWorkshop;
	}

	public Integer getRegistrationAmount() {
		return registrationAmount;
	}

	public void setRegistrationAmount(Integer registrationAmount) {
		this.registrationAmount = registrationAmount;
	}

	public Integer getAdditionalTicketsWelcome() {
		return additionalTicketsWelcome;
	}

	public void setAdditionalTicketsWelcome(Integer additionalTicketsWelcome) {
		this.additionalTicketsWelcome = additionalTicketsWelcome;
	}

	public Integer getAdditionalTicketsDinner() {
		return additionalTicketsDinner;
	}

	public void setAdditionalTicketsDinner(Integer additionalTicketsDinner) {
		this.additionalTicketsDinner = additionalTicketsDinner;
	}

	public String getPaypalItemName() {
		return paypalItemName;
	}

}
