package org.acrs.portlets;


import java.text.DateFormat;
import java.util.Date;

import org.acrs.data.model.ConferenceRegistration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConferenceRegistrationTests {
	
	final int STUDENT_MEMBER_EARLY = 380;
	final int STUDENT_NON_MEMBER_EARLY = 410;
	final int FULL_MEMBER_EARLY = 490;
	final int FULL_NON_MEMBER_EARLY = 530;
	final int STUDENT_MEMBER = 400;
	final int STUDENT_NON_MEMBER = 430;
	final int FULL_MEMBER = 510;
	final int FULL_NON_MEMBER = 550;
	final int STUDENT_MENTORING_DISCOUNT = 70;
	final int CORAL_IDENTIFICATION_WORKSHOP = 330;
	final int WELCOME_FUNCTION = 25;
	final int CONFERENCE_DINNER = 99;
	Date EARLY_BIRD;
	

	ConferenceRegistrationPortlet crp;
	ConferenceRegistration registration;
	@Before
	public void setUp() throws Exception {
		this.crp = new ConferenceRegistrationPortlet();
		Date early = DateFormat.getDateInstance().parse("2013-05-14");
		this.registration = new ConferenceRegistration(new FakeClock(early));
		 EARLY_BIRD = DateFormat.getDateInstance().parse("2013-06-15"); // on or before
	}

	
	@Test
	public void testInvalidInput() {
		
	}
	
	
	@Test
	public void testPriceInvalid() {
		
	}
	
	/**
	 * Student registration
	 */
	@Test
	public void calculateStudentMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentMember");
		registration.calculateRegistration();
		assertEquals(330, (int)registration.getRegistrationAmount());
	}
	
	/**
	 * Student non-member registration
	 */
	@Test
	public void calculateStudentNonMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentNonMember");
		registration.calculateRegistration();
		assertEquals(STUDENT_NON_MEMBER_EARLY, (int)registration.getRegistrationAmount());
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
	 * Student (330), 1 welcome (35), 2 dinner (60)
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
