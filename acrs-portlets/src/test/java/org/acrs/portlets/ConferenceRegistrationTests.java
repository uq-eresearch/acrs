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
	
	@Test
	public void calculateStudentMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentMember");
		registration.calculateRegistration();
		assertEquals(330, (int)registration.getRegistrationAmount());
	}
	
	@Test
	public void calculateStudentNonMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("StudentNonMember");
		registration.calculateRegistration();
		assertEquals(380, (int)registration.getRegistrationAmount());
	}
	
	@Test
	public void calculateFullMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullMember");
		registration.calculateRegistration();
		assertEquals(440, (int)registration.getRegistrationAmount());
	}
	
	@Test
	public void calculateFullNonMemberPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullNonMember");
		registration.calculateRegistration();
		assertEquals(499, (int)registration.getRegistrationAmount());
	}
	
	@Test
	public void calculateDayOneOnlyPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("DayOneOnly");
		registration.calculateRegistration();
		assertEquals(240, (int)registration.getRegistrationAmount());
	}
	
	@Test
	public void calculateDayTwoOnlyPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("DayTwoOnly");
		registration.calculateRegistration();
		assertEquals(240, (int)registration.getRegistrationAmount());
	}
	

	@Test
	public void calculateFullMemberTwoDinnerPrice() throws RegistrationProcessingException {
		registration.setRegistrationRate("FullMember");
		registration.setAdditionalTicketsDinner(2);
		registration.calculateRegistration();
		assertEquals(460, (int)registration.getRegistrationAmount());
	}
	
}
