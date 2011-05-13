package org.acrs.portlets;


import org.acrs.data.model.ConferenceRegistration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConferenceRegistrationTests {

	ConferenceRegistrationPortlet crp;
	ConferenceRegistration registration;
	@Before
	public void setUp() throws Exception {
		this.crp = new ConferenceRegistrationPortlet();
		this.registration = new ConferenceRegistration();
	}

	
	@Test
	public void testInvalidInput() {
		
	}
	
	
	@Test
	public void testPriceInvalid() {
		
	}
	
	/**
	 * Student registration is $330
	 */
	@Test
	public void calculateStudentMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentMember");
		registration.calculateRegistration();
		assertEquals(330, (int)registration.getRegistrationAmount());
	}
	
	/**
	 * Student non-member registration is $380
	 */
	@Test
	public void calculateStudentNonMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentNonMember");
		registration.calculateRegistration();
		assertEquals(380, (int)registration.getRegistrationAmount());
	}

	/**
	 * Full registration is $440
	 */
	@Test
	public void calculateFullMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullMember");
		registration.calculateRegistration();
		assertEquals(440, (int)registration.getRegistrationAmount());
	}

	/**
	 * Full non-member registration is $499
	 */
	@Test
	public void calculateFullNonMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullNonMember");
		registration.calculateRegistration();
		assertEquals(499, (int)registration.getRegistrationAmount());
	}

	/**
	 * Day one only is $240
	 */
	@Test
	public void calculateDayOneOnlyPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("DayOneOnly");
		registration.calculateRegistration();
		assertEquals(240, (int)registration.getRegistrationAmount());
	}


	/**
	 * Day two only is $240
	 */
	@Test
	public void calculateDayTwoOnlyPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("DayTwoOnly");
		registration.calculateRegistration();
		assertEquals(240, (int)registration.getRegistrationAmount());
	}
	

	/**
	 * Full member rate is 440, dinner tickets are 60ea
	 */
	@Test
	public void calculateFullMemberTwoDinnerPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullMember");
		registration.setAdditionalTicketsDinner(2);
		registration.calculateRegistration();
		assertEquals(560, (int)registration.getRegistrationAmount());
	}

	/**
	 * Full non-member rate is 499, Welcome tickets are 35ea
	 */
	@Test
	public void calculateFullNonMemberTwoWecomePrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullNonMember");
		registration.setAdditionalTicketsWelcome(2);
		registration.calculateRegistration();
		assertEquals(569, (int)registration.getRegistrationAmount());
	}

	/**
	 * Full member rate is 440, dinner tickets are 60ea, coral finder workshop is 250
	 */
	@Test
	public void calculateFullMemberTwoDinnerPriceCoralFinderWorkshop() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullMember");
		registration.setAdditionalTicketsDinner(2);
		registration.setCoralFinderWorkshop(true);
		registration.calculateRegistration();
		assertEquals(810, (int)registration.getRegistrationAmount());
	}
	
	/**
	 * Student (330), coral finder (250), 1 welcome (35), 2 dinner (60)
	 */
	@Test
	public void calculateStudentCoralWelcomeDinner() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentMember");
		registration.setAdditionalTicketsWelcome(1);
		registration.setAdditionalTicketsDinner(2);
		registration.setCoralFinderWorkshop(true);
		registration.setStudentMentoringDay(true);
		registration.calculateRegistration();
		assertEquals(685, (int)registration.getRegistrationAmount());
	}
	
}
