package org.acrs.portlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.acrs.app.ACRSApplication;
import org.acrs.data.access.ConferenceRegistrationDao;
import org.acrs.data.model.ConferenceRegistration;
import org.acrs.util.Emailer;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Author: alabri Date: 08/02/2011 Time: 4:26:58 PM
 */
public class ConferenceRegistrationPortlet extends GenericPortlet {
	private static Log _log = LogFactoryUtil
			.getLog(ConferenceRegistrationPortlet.class);
	protected String viewJSP;
	protected ConferenceRegistrationDao membersDao;

	public void init() throws PortletException {
		viewJSP = getInitParameter("confreg-jsp");
		membersDao = ACRSApplication.getConfiguration()
				.getConferenceRegistrationDao();
	}

	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		// Do all you views here

		// if the user is a liferay admin list the membership database in the
		// JSP.
		boolean isAdmin = renderRequest.isUserInRole("administrator");
		renderRequest.setAttribute("isAdmin", isAdmin);

		List<ConferenceRegistration> allMembers = membersDao.getAll();
		renderRequest.setAttribute("allMembers", allMembers);
		renderRequest.setAttribute("membersDao", membersDao);

		include(viewJSP, renderRequest, renderResponse);
	}

	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		// Process data here
		PortletSession session = actionRequest.getPortletSession(true);
		String editMemberIdStr = actionRequest.getParameter("editMemberId");

		String action = "";

		if (editMemberIdStr == null) {
			action = "ADD";
			_log.info("request to add new member");
		} else {
			action = "EDIT";
			_log.info("edit member id " + editMemberIdStr);
		}

		ConferenceFormBean formBean = extractRequestParameters(actionRequest);

		List<String> errors = checkFormForErrors(actionRequest);

		if (errors.size() > 0) {

			actionRequest.setAttribute("errors", errors);
		} else {

			if (action.equals("ADD")) {
				addNewMember(actionResponse, session, formBean);
			} else if (action.equals("EDIT")) {
				editMembership(actionRequest, formBean);
			}

		} // end if ! errors

	}

	private List<String> checkFormForErrors(ActionRequest actionRequest) {
		List<String> errors = new ArrayList<String>();
		String firstName = actionRequest.getParameter("firstName");
		String lastName = actionRequest.getParameter("lastName");
		String streetAddress = actionRequest.getParameter("streetAddress")
				+ " " + actionRequest.getParameter("streetAddress2");
		String city = actionRequest.getParameter("city");
		String state = actionRequest.getParameter("state");
		String postcode = actionRequest.getParameter("postcode");
		String country = actionRequest.getParameter("country");
		String email = actionRequest.getParameter("email");
		String phone = actionRequest.getParameter("phone");
		String institution = actionRequest.getParameter("institution");
		String researchInterest = actionRequest
				.getParameter("researchInterest");
		String membershipType = actionRequest.getParameter("membershipType");

		if ((firstName == null) || firstName.isEmpty()
				|| (streetAddress == null) || streetAddress.isEmpty()
				|| (city == null) || city.isEmpty() || (state == null)
				|| state.isEmpty() || (postcode == null) || postcode.isEmpty()
				|| (country == null) || country.isEmpty() || (email == null)
				|| email.isEmpty() || (phone == null) || phone.isEmpty()
				|| (institution == null) || institution.isEmpty()
				|| (researchInterest == null) || researchInterest.isEmpty()
				|| (membershipType == null) || membershipType.isEmpty()) {

			errors.add("All fields are required.");
		}

		if ((firstName.contains("<")) || (firstName.contains(">"))
				|| (lastName.contains("<")) || (lastName.contains(">"))
				|| (streetAddress.contains("<"))
				|| (streetAddress.contains(">")) || (city.contains("<"))
				|| (city.contains(">")) || (state.contains("<"))
				|| (state.contains(">")) || (email.contains("<"))
				|| (email.contains(">")) || (phone.contains("<"))
				|| (phone.contains(">")) || (institution.contains("<"))
				|| (institution.contains(">"))
				|| (researchInterest.contains("<"))
				|| (researchInterest.contains(">"))) {
			errors.add("Please, remove these characters from the form before continuing: < >");
		}

		if ((email == null) || email.isEmpty()) {
			errors.add("Please include a valid email address.");
		}
		return errors;
	}

	private void editMembership(ActionRequest actionRequest,
			ConferenceFormBean crb) {

		_log.info("editing.");

		long editMemberId = Long.parseLong(actionRequest
				.getParameter("editMemberId"));
		ConferenceRegistration editMember = membersDao.getById(editMemberId);
		Double membershipAmount = Double.parseDouble(actionRequest
				.getParameter("membershipAmount"));
		String paypalStatus = actionRequest.getParameter("paypalStatus");
		String removeFlag = actionRequest.getParameter("removeFlag");
		List<String> messages = new ArrayList<String>();
		Date now = new Date();

		try {
			BeanUtils.copyProperties(editMember, crb);
		} catch (IllegalAccessException e) {
			_log.error("Error copying properties", e);
		} catch (InvocationTargetException e) {
			_log.error("Error copying properties", e);
		}

		editMember.setMembershipAmount(membershipAmount);
		editMember.setPaypalStatus(paypalStatus);
		editMember.setUpdateDate(now);
		_log.info("checking remove flag.");
		if (removeFlag.equals("Y")) {
			editMember.setIsActive(false);
			messages.add("Member record for " + editMember.getFirstName() + " "
					+ editMember.getLastName() + " has been deactivated.");
		}
		membersDao.save(editMember);
		_log.info("updated memeber.");
		messages.add("Member record for " + editMember.getFirstName() + " "
				+ editMember.getLastName() + " has been updated.");
		actionRequest.setAttribute("messages", messages);

	}

	private void addNewMember(ActionResponse actionResponse,
			PortletSession session, ConferenceFormBean crb) {

		ConferenceRegistration newMember = new ConferenceRegistration();

		String membershipType = crb.getMembershipType();

		// calculate membership amount
		Double membershipAmount = 0.00;
		String paypalItemName = "Australian Coral Reef Society ";
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date startDiscountDate = new Date();
		Date endDiscountDate = new Date();

		if (membershipType.equals("Full")) {
			membershipAmount = 50.00;
			paypalItemName = paypalItemName + "Full Membership for "
					+ Integer.toString(thisYear);

		} else if (membershipType.equals("Student")) {
			membershipAmount = 30.00;
			paypalItemName = paypalItemName + "Student Membership for "
					+ Integer.toString(thisYear);
		} else if (membershipType.equals("FiveYear")) {
			membershipAmount = 200.00;
			paypalItemName = paypalItemName + "Full 5 Year Membership from "
					+ Integer.toString(thisYear);
		} else if (membershipType.equals("Test")) {
			membershipAmount = 5.00;
			paypalItemName = paypalItemName + "Test Membership from "
					+ Integer.toString(thisYear);
		}

		try {
			startDiscountDate = sdf
					.parse("01-01-" + Integer.toString(thisYear));
			endDiscountDate = sdf.parse("01-03-" + Integer.toString(thisYear));
		} catch (ParseException e) {
			_log.error("Error parsing date", e);
		}

		if (!(membershipType.equals("FiveYear"))
				&& (newMember.getRegistrationDate().after(startDiscountDate))
				&& (newMember.getRegistrationDate().before(endDiscountDate))) {
			membershipAmount = membershipAmount - 10.00;
			paypalItemName = paypalItemName + " (with early discount)";
		}

		// save member details
		try {
			BeanUtils.copyProperties(newMember, crb);
		} catch (Exception e1) {
			_log.error("Error copying properties", e1);
		}

		newMember.setMembershipType(membershipType);
		newMember.setPaypalRef("");
		newMember.setPaypalStatus("Unverified");
		newMember.setIsActive(true);
		newMember.setUpdateDate(newMember.getRegistrationDate());

		membersDao.save(newMember);

		// email stuff out
		String approvalEmail1 = ACRSApplication.getConfiguration()
				.getApprovalEmail1();
		String approvalEmail2 = ACRSApplication.getConfiguration()
				.getApprovalEmail2();
		String emailListCoordEmail = ACRSApplication.getConfiguration()
				.getEmailListCoordEmail();

		String approvalMessage = "Hi ACRS, \n\nPlease find below details of an application for membership that has been submitted. \n\nKind Regards, \nThe ACRS Website\n\n";
		String emailListMessage = "Hi, \n\nThe following membership applicant indicated a desire to subscribe to the ACRS Mailing List. \n\nKind Regards, \nThe ACRS Website\n\n";
		String applicantDetail = "\n\tName:\t\t\t\t" + newMember.getTitle()
				+ " " + newMember.getFirstName() + " "
				+ newMember.getLastName() + "\n\tAddress:\t\t\t"
				+ newMember.getStreetAddress() + ", " + newMember.getCity()
				+ " " + newMember.getState() + " " + newMember.getPostcode()
				+ "\n\tEmail:\t\t\t" + newMember.getEmail()
				+ "\n\tPhone:\t\t\t" + newMember.getPhone()
				+ "\n\tInstitution:\t\t" + newMember.getInstitution()
				+ "\n\tResearch Interest:\t" + newMember.getResearchInterest()
				+ "\n\tNewsletter Preference:\t"
				+ newMember.getNewsletterPref() + "\n\tMembership Type: \t\t"
				+ newMember.getMembershipType() + "\n\tMembership Amount: \t"
				+ newMember.getMembershipAmount() + "0";

		try {
			Emailer.sendEmail(approvalEmail1, "no-reply@acrs.org",
					"New ACRS Membership", approvalMessage + applicantDetail);
			Emailer.sendEmail(approvalEmail2, "no-reply@acrs.org",
					"New ACRS Membership", approvalMessage + applicantDetail);

			if ("Y".equals(newMember.getAcrsEmailListFlag())) {
				Emailer.sendEmail(emailListCoordEmail, "no-reply@acrs.org",
						"New ACRS Mail List Subscribe Request",
						emailListMessage + applicantDetail);
			}

		} catch (MessagingException e) {
			_log.fatal("Could not send email.");
		}

		actionResponse.setRenderParameter("newMemberId",
				String.valueOf(newMember.getId()));
		session.setAttribute("newMember", newMember,
				PortletSession.APPLICATION_SCOPE);
		session.setAttribute("paypalItemName", paypalItemName,
				PortletSession.APPLICATION_SCOPE);

	}

	private ConferenceFormBean extractRequestParameters(
			ActionRequest actionRequest) {
		ConferenceFormBean crb = new ConferenceFormBean();
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		Enumeration<String> names = actionRequest.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, actionRequest.getParameterValues(name));
		}
		try {
			BeanUtils.populate(crb, map);
		} catch (Exception e1) {
			_log.error("Error loading parameters", e1);
		}
		crb.setStreetAddress(crb.getStreetAddress() + " "
				+ crb.getStreetAddress2());
		crb.setNewsletterPref(crb.getNewsletterPref() + ", "
				+ crb.getNewsletterPref2());

		// tidy up nulls
		if (crb.getAcrsEmailListFlag() == null
				|| crb.getAcrsEmailListFlag().isEmpty()) {
			crb.setAcrsEmailListFlag("N");
		}

		if (crb.getRenewalFlag() == null || crb.getRenewalFlag().isEmpty()) {
			crb.setAcrsEmailListFlag("N");
		}
		return crb;
	}

	protected void include(String path, RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {

		PortletContext portletContext = getPortletContext();
		PortletRequestDispatcher portletRequestDispatcher = portletContext
				.getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		} else {
			try {
				portletRequestDispatcher.include(renderRequest, renderResponse);
			} catch (Exception e) {
				_log.error(e, e);
				portletRequestDispatcher = portletContext
						.getRequestDispatcher("/error.jsp");

				if (portletRequestDispatcher == null) {
					_log.error("/error.jsp is not a valid include");
				} else {
					portletRequestDispatcher.include(renderRequest,
							renderResponse);
				}
			}
		}
	}

	public void destroy() {
		if (_log.isInfoEnabled()) {
			_log.info("Destroying portlet");
		}
	}

	public void serveResource(ResourceRequest req, ResourceResponse res)
			throws PortletException, IOException {

		// create an excel file containing the member list for the user to
		// download
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet("ACRS Member Database "
				+ sdf.format(new Date()));
		s.setFitToPage(true);
		HSSFRow r = null;
		HSSFCell c = null;

		// Header
		HSSFRow th = s.createRow(0);
		HSSFCellStyle thStyle = wb.createCellStyle();
		HSSFFont f = wb.createFont();
		f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		thStyle.setFont(f);

		ArrayList<String> headings = new ArrayList<String>();
		headings.add("Title");
		headings.add("First Name");
		headings.add("Last Name");
		headings.add("Street Address");
		headings.add("City");
		headings.add("State");
		headings.add("Postcode");
		headings.add("Country");
		headings.add("Email");
		headings.add("Phone");
		headings.add("Institution");
		headings.add("Research Interest");
		headings.add("Newsletter Preference");
		headings.add("ACRS Email List Flag");
		headings.add("Membership Type");
		headings.add("Renewal Flag");
		headings.add("Membership Amount");
		headings.add("Registration Date");
		headings.add("Paypal Status");
		headings.add("Paypal Confirmation Details");

		// write to sheet
		for (String h : headings) {
			c = th.createCell(headings.indexOf(h));
			HSSFRichTextString rts = new HSSFRichTextString(h);
			c.setCellValue(rts);
			c.setCellStyle(thStyle);
		}

		// add members
		List<ConferenceRegistration> allMembers = membersDao.getAll();
		for (ConferenceRegistration member : allMembers) {

			ArrayList<String> a = new ArrayList<String>();
			a.add(member.getTitle());
			a.add(member.getFirstName());
			a.add(member.getLastName());
			a.add(member.getStreetAddress());
			a.add(member.getCity());
			a.add(member.getState());
			a.add(member.getPostcode());
			a.add(member.getCountry());
			a.add(member.getEmail());
			a.add(member.getPhone());
			a.add(member.getInstitution());
			a.add(member.getResearchInterest());
			a.add(member.getNewsletterPref());
			a.add(member.getAcrsEmailListFlag());
			a.add(member.getMembershipType());
			a.add(member.getRenewalFlag());
			a.add(member.getMembershipAmount() + "0");
			a.add(member.getRegistrationDate().toString());
			a.add(member.getPaypalStatus());
			a.add(member.getPaypalRef());

			r = s.createRow(allMembers.indexOf(member) + 1);
			Iterator<String> i = a.iterator();
			int cellNum = 0;

			while (i.hasNext()) {
				c = r.createCell(cellNum);
				HSSFRichTextString rts = new HSSFRichTextString(i.next());
				c.setCellValue(rts);
				s.autoSizeColumn((short) cellNum);
				cellNum++;
			}

			/*
			 * for (String m : a) {
			 * 
			 * c = r.createCell(a.indexOf(m)); HSSFRichTextString rts = new
			 * HSSFRichTextString(m); c.setCellValue(rts);
			 * s.autoSizeColumn((short) a.indexOf(m)); }
			 */

		}

		// write workbook out to file
		FileOutputStream fos = new FileOutputStream("MemberList.xls");
		wb.write(fos);
		fos.flush();
		fos.close();

		File file = new File("MemberList.xls");
		FileInputStream fileIn = new FileInputStream(file);
		res.setContentType("application/vnd.ms-excel");

		OutputStream out = res.getPortletOutputStream();

		byte[] outputByte = new byte[4096];
		while (fileIn.read(outputByte, 0, 4096) != -1) {
			out.write(outputByte, 0, 4096);
		}

		fileIn.close();

		out.flush();
		out.close();

		file.delete();

	}

	class ConferenceFormBean {
		String title;
		String firstName;
		String lastName;
		String streetAddress;
		String streetAddress2;
		String city;
		String state;
		String postcode;
		String country;
		String email;
		String phone;
		String institution;
		String researchInterest;
		String newsletterPref;
		String newsletterPref2;
		String membershipType;
		String renewalFlag;
		String acrsEmailListFlag;

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

		public String getResearchInterest() {
			return researchInterest;
		}

		public void setResearchInterest(String researchInterest) {
			this.researchInterest = researchInterest;
		}

		public String getNewsletterPref() {
			return newsletterPref;
		}

		public void setNewsletterPref(String newsletterPref) {
			this.newsletterPref = newsletterPref;
		}

		public String getNewsletterPref2() {
			return newsletterPref2;
		}

		public void setNewsletterPref2(String newsletterPref2) {
			this.newsletterPref2 = newsletterPref2;
		}

		public String getMembershipType() {
			return membershipType;
		}

		public void setMembershipType(String membershipType) {
			this.membershipType = membershipType;
		}

		public String getRenewalFlag() {
			return renewalFlag;
		}

		public void setRenewalFlag(String renewalFlag) {
			this.renewalFlag = renewalFlag;
		}

		public String getAcrsEmailListFlag() {
			return acrsEmailListFlag;
		}

		public void setAcrsEmailListFlag(String acrsEmailListFlag) {
			this.acrsEmailListFlag = acrsEmailListFlag;
		}
	}

}
