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
	private String email = "";
	private String institution = "";
	private String submittingAbstract = "";
	private String registrationRate = "";
	private String attendStudentMentoringDay = "";
	private String studentMentoringDiscount = "";
	private String simsExcursion = "";
	private String coralIdentificationWorkshop = "";
	private String additionalTicketsWelcome = "";
	private String additionalTicketsDinner = "";
	private String specialFoodRequirements = "";
	private String phone = "";
	private String shareWith = "";
        private String behaviourDelegate = "";
        private String arrivalDateDaydreamIsland = "";
        private String departureDateDaydreamIsland = "";
        private String boatTransfers = "";
        private String specialWheelchairRequirements = "";

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
			_log.error("Error copying properties", e);
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

	public String getCoralIdentificationWorkshop() {
		return coralIdentificationWorkshop;
	}

	public void setCoralIdentificationWorkshop(String coralIdentificationWorkshop) {
		this.coralIdentificationWorkshop = coralIdentificationWorkshop;
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

	public void setAttendStudentMentoringDay(String attendStudentMentoringDay) {
		this.attendStudentMentoringDay = attendStudentMentoringDay;
	}

	public String getAttendStudentMentoringDay() {
		return attendStudentMentoringDay;
	}

	public void setStudentMentoringDiscount(String studentMentoringDiscount) {
		this.studentMentoringDiscount = studentMentoringDiscount;
	}

	public String getStudentMentoringDiscount() {
		return studentMentoringDiscount;
	}

	public String getSimsExcursion() {
		return simsExcursion;
	}

	public void setSimsExcursion(String simsExcursion) {
		this.simsExcursion = simsExcursion;
	}

	public String getSpecialFoodRequirements() {
		return specialFoodRequirements;
	}

	public void setSpecialFoodRequirements(String specialFoodRequirements) {
		this.specialFoodRequirements = specialFoodRequirements;
	}

	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShareWith() {
		return this.shareWith;
	}
	public void setShareWith(String shareWith) {
		this.shareWith = shareWith;
	}

    /**
     * @return the behaviourDelegate
     */
    public String getBehaviourDelegate() {
        return behaviourDelegate;
    }

    /**
     * @param behaviourDelegate the behaviourDelegate to set
     */
    public void setBehaviourDelegate(String behaviourDelegate) {
        this.behaviourDelegate = behaviourDelegate;
    }

    /**
     * @return the arrivalDateDaydreamIsland
     */
    public String getArrivalDateDaydreamIsland() {
        return arrivalDateDaydreamIsland;
    }

    /**
     * @param arrivalDateDaydreamIsland the arrivalDateDaydreamIsland to set
     */
    public void setArrivalDateDaydreamIsland(String arrivalDateDaydreamIsland) {
        this.arrivalDateDaydreamIsland = arrivalDateDaydreamIsland;
    }

    /**
     * @return the departureDateDaydreamIsland
     */
    public String getDepartureDateDaydreamIsland() {
        return departureDateDaydreamIsland;
    }

    /**
     * @param departureDateDaydreamIsland the departureDateDaydreamIsland to set
     */
    public void setDepartureDateDaydreamIsland(String departureDateDaydreamIsland) {
        this.departureDateDaydreamIsland = departureDateDaydreamIsland;
    }

    /**
     * @return the boatTransfers
     */
    public String getBoatTransfers() {
        return boatTransfers;
    }

    /**
     * @param boatTransfers the boatTransfers to set
     */
    public void setBoatTransfers(String boatTransfers) {
        this.boatTransfers = boatTransfers;
    }

    /**
     * @return the specialWheelchairRequirements
     */
    public String getSpecialWheelchairRequirements() {
        return specialWheelchairRequirements;
    }

    /**
     * @param specialWheelchairRequirements the specialWheelchairRequirements to set
     */
    public void setSpecialWheelchairRequirements(String specialWheelchairRequirements) {
        this.specialWheelchairRequirements = specialWheelchairRequirements;
    }
        
}