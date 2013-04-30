package org.acrs.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;
	private String firstName;
	private String lastName;
	private String email;

	@Column(length = 500)
	private String institution;

	private Boolean submittingAbstract;

	private String registrationRate;

	private Boolean attendStudentMentoringDay;
	private Boolean studentMentoringDiscount;
	private Boolean simsExcursion;
	private Boolean coralIdentificationWorkshop;
	private Integer additionalTicketsWelcome;
	private Integer additionalTicketsDinner;
	
	private String specialFoodRequirements;

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
	
	@Transient
	private Clock clock;
	
	public ConferenceRegistration() {
		// Should only be used by hibernate when loading data
	}

	public ConferenceRegistration(Clock clock) {
		this.clock = clock;
		this.registrationDate = this.clock.getCurrentDate();

	}

	public ConferenceRegistration(ConferenceFormBean cfb, Clock clock)
			throws RegistrationProcessingException {
		this.clock = clock;
		this.registrationDate = this.clock.getCurrentDate();
		
		try {
			BeanUtils.copyProperties(this, cfb);
		} catch (Exception e1) {
			throw new RegistrationProcessingException(
					"Error creating database record" + e1);
		}
	}

	public void calculateRegistration() throws RegistrationProcessingException {
		// calculate Registration amount
		paypalItemName = "Australian Coral Reef Society Conference 2013";
		registrationAmount = 0;


		if ("StudentMember".equals(registrationRate)) {
			registrationAmount = 380;
			paypalItemName += " Student Member Registration";
		} else if ("StudentNonMember".equals(registrationRate)) {
			registrationAmount = 410;
			paypalItemName += " Student Non-Member Registration";
		} else if ("FullMember".equals(registrationRate)) {
			registrationAmount = 490;
			paypalItemName += " Full Member Registration";
		} else if ("FullNonMember".equals(registrationRate)) {
			registrationAmount = 530;
			paypalItemName += " Full Non-Member Registration";
		} else {
			throw new RegistrationProcessingException(
					"Can't calculate registration rate for: "
							+ registrationRate);
		}
		
		try {
			Date earlyBirdDate = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-16");
			if (this.registrationDate.after(earlyBirdDate)) {
				registrationAmount += 20;
				paypalItemName += " (Late Registration)";
			} else {
				paypalItemName += " (Early-bird Registration)";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		if (this.getStudentMentoringDiscount()) {
			registrationAmount -= 70;
			paypalItemName += " - Student Mentoring Discount";
		}

		if (this.getCoralIdentificationWorkshop()) {
			registrationAmount += 330;
			paypalItemName += " + Coral Finder Workshop";
		}

		if (this.getAdditionalTicketsWelcome() > 0) {
			registrationAmount += this.getAdditionalTicketsWelcome() * 25;
			paypalItemName += " + " + this.getAdditionalTicketsWelcome()
					+ " Welcome Event Tickets";
		}

		if (this.getAdditionalTicketsDinner() > 0) {
			registrationAmount += this.getAdditionalTicketsDinner() * 99;
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Boolean getAttendStudentMentoringDay() {
		return attendStudentMentoringDay != null ? attendStudentMentoringDay : false;
	}

	public void setAttendStudentMentoringDay(Boolean studentMentoringDay) {
		this.attendStudentMentoringDay = studentMentoringDay;
	}

	public Boolean getStudentMentoringDiscount() {
		return studentMentoringDiscount != null ? studentMentoringDiscount : false;
	}

	public void setStudentMentoringDiscount(Boolean studentMentoringDiscount) {
		this.studentMentoringDiscount = studentMentoringDiscount;
	}

	public Boolean getCoralIdentificationWorkshop() {
		return coralIdentificationWorkshop != null ? coralIdentificationWorkshop : false;
	}

	public void setCoralIdentificationWorkshop(Boolean coralIdentificationWorkshop) {
		this.coralIdentificationWorkshop = coralIdentificationWorkshop;
	}

	public Integer getRegistrationAmount() {
		return registrationAmount;
	}

	public void setRegistrationAmount(Integer registrationAmount) {
		this.registrationAmount = registrationAmount;
	}

	public Integer getAdditionalTicketsWelcome() {
		return additionalTicketsWelcome != null ? additionalTicketsWelcome : 0;
	}

	public void setAdditionalTicketsWelcome(Integer additionalTicketsWelcome) {
		this.additionalTicketsWelcome = additionalTicketsWelcome;
	}

	public Integer getAdditionalTicketsDinner() {
		return additionalTicketsDinner != null ? additionalTicketsDinner : 0;
	}

	public void setAdditionalTicketsDinner(Integer additionalTicketsDinner) {
		this.additionalTicketsDinner = additionalTicketsDinner;
	}

	public String getSpecialFoodRequirements() {
		return specialFoodRequirements;
	}

	public void setSpecialFoodRequirements(String specialFoodRequirements) {
		this.specialFoodRequirements = specialFoodRequirements;
	}

	public String getPaypalItemName() {
		return paypalItemName;
	}

	public Boolean getSimsExcursion() {
		return simsExcursion;
	}

	public void setSimsExcursion(Boolean simsExcursion) {
		this.simsExcursion = simsExcursion;
	}

}
