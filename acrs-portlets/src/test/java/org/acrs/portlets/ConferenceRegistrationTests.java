package org.acrs.portlets;


import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.acrs.data.model.ConferenceRegistration;
import org.junit.Before;
import org.junit.Test;

public class ConferenceRegistrationTests {
	
	final int STUDENT_MEMBER_EARLY = 380;
	final int STUDENT_NON_MEMBER_EARLY = 410;
	final int FULL_MEMBER_EARLY = 490;
	final int FULL_NON_MEMBER_EARLY = 530;
	final int STUDENT_MEMBER = 400;
	final int STUDENT_NON_MEMBER = 430;
	final int FULL_MEMBER = 510;
	final int FULL_NON_MEMBER = 550;
	final int STUDENT_MENTORING_DISCOUNT = -70;
	final int CORAL_IDENTIFICATION_WORKSHOP = 330;
	final int WELCOME_FUNCTION = 25;
	final int CONFERENCE_DINNER = 99;
	final int LATE_SURCHARGE = 20;
	

	ConferenceRegistrationPortlet crp;
	ConferenceRegistration registration;
	@Before
	public void setUp() throws Exception {
		this.crp = new ConferenceRegistrationPortlet();
		Date early = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2013-06-15 12:33");
		registration = new ConferenceRegistration(new FakeClock(early));
		
	}
	
	/**
	 * Student registration
	 */
	@Test
	public void calculateStudentMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentMember");
		registration.calculateRegistration();
		assertEquals(STUDENT_MEMBER_EARLY, (int)registration.getRegistrationAmount());
	}
	
	@Test
	public void calculateLateStudentMemberPrice() throws RegistrationProcessingException, ParseException {
		Date late = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2013-06-16 00:33");
		registration = new ConferenceRegistration(new FakeClock(late));
		registration.setRegistrationRate("StudentMember");
		registration.calculateRegistration();
		assertEquals(STUDENT_MEMBER, (int)registration.getRegistrationAmount());
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


	@Test
	public void calculateFullMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullMember");
		registration.calculateRegistration();
		assertEquals(FULL_MEMBER_EARLY, (int)registration.getRegistrationAmount());
	}


	@Test
	public void calculateFullNonMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullNonMember");
		registration.calculateRegistration();
		assertEquals(FULL_NON_MEMBER_EARLY, (int)registration.getRegistrationAmount());
	}


	@Test
	public void calculateFullMemberTwoDinnerPrice() throws RegistrationProcessingException {
		int price = FULL_MEMBER_EARLY + 2 * CONFERENCE_DINNER;
		registration.setRegistrationRate("FullMember");
		registration.setAdditionalTicketsDinner(2);
		registration.calculateRegistration();
		assertEquals(price, (int)registration.getRegistrationAmount());
	}

	@Test
	public void calculateFullNonMemberTwoWecomePrice() throws RegistrationProcessingException {
		int price = FULL_NON_MEMBER_EARLY + 2 * WELCOME_FUNCTION;
		registration.setRegistrationRate("FullNonMember");
		registration.setAdditionalTicketsWelcome(2);
		registration.calculateRegistration();
		assertEquals(price, (int)registration.getRegistrationAmount());
	}

	@Test
	public void calculateFullMemberTwoDinnerPriceCoralFinderWorkshop() throws RegistrationProcessingException {
		int price = FULL_MEMBER_EARLY + 3 * CONFERENCE_DINNER + CORAL_IDENTIFICATION_WORKSHOP;
		registration.setRegistrationRate("FullMember");
		registration.setAdditionalTicketsDinner(3);
		registration.setCoralIdentificationWorkshop(true);
		registration.calculateRegistration();
		assertEquals(price, (int)registration.getRegistrationAmount());
	}

	@Test
	public void calculateStudentCoralWelcomeDinner() throws RegistrationProcessingException {
		int price = STUDENT_MEMBER_EARLY + WELCOME_FUNCTION + 2 * CONFERENCE_DINNER + CORAL_IDENTIFICATION_WORKSHOP + STUDENT_MENTORING_DISCOUNT;
		registration.setRegistrationRate("StudentMember");
		registration.setAdditionalTicketsWelcome(1);
		registration.setAdditionalTicketsDinner(2);
		registration.setCoralIdentificationWorkshop(true);
		registration.setStudentMentoringDiscount(true);
		registration.calculateRegistration();
		assertEquals(price, (int)registration.getRegistrationAmount());
	}
	
}
