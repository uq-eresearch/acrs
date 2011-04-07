package org.acrs.portlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.acrs.app.ACRSApplication;
import org.acrs.data.access.ConferenceRegistrationDao;
import org.acrs.data.model.ConferenceRegistration;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * Author: alabri Date: 08/02/2011 Time: 4:26:58 PM
 */
public class ConferenceRegistrationPortlet extends GenericPortlet {
	private static Log _log = LogFactoryUtil
			.getLog(ConferenceRegistrationPortlet.class);
	protected String viewJSP;
	protected ConferenceRegistrationDao conferenceRegistrationDao;

	public void init() throws PortletException {
		viewJSP = getInitParameter("confreg-jsp");
		conferenceRegistrationDao = ACRSApplication.getConfiguration()
				.getConferenceRegistrationDao();
	}

	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		
		String editJSP = "/WEB-INF/jsp/addeditconf.jsp";
		String registrationsListJSP = "/WEB-INF/jsp/registrations.jsp";
		String submitVerify = "/WEB-INF/jsp/submit.jsp";

		String cmd = ParamUtil.getString(renderRequest, "cmd");
		// Do all you views here

		// if the user is a liferay admin list the registration database in the
		// JSP.
		boolean isAdmin = renderRequest.isUserInRole("administrator");
		renderRequest.setAttribute("isAdmin", isAdmin);

		if (isAdmin) {
			if ("EDIT".equals(cmd)) {
				long registrationId = ParamUtil.getLong(renderRequest, "registrationId");
				ConferenceRegistration editRegistration = conferenceRegistrationDao.getById(registrationId);
				
				ConferenceFormBean formBean = new ConferenceFormBean();
				try {
					BeanUtils.copyProperties(formBean, editRegistration);
				} catch (IllegalAccessException e) {
					_log.error("Error copying properties", e);
				} catch (InvocationTargetException e) {
					_log.error("Error copying properties", e);
				}

				renderRequest.setAttribute("formBean", formBean);
				renderRequest.setAttribute("editRegistration", editRegistration);
				
				include(editJSP, renderRequest, renderResponse);

			} else {
				List<ConferenceRegistration> allRegistrations = conferenceRegistrationDao.getAll();
				renderRequest.setAttribute("allRegistrations", allRegistrations);
	
				include(registrationsListJSP, renderRequest, renderResponse);
			}
		} else {
			ConferenceRegistration newRegistration = (ConferenceRegistration) renderRequest.getPortletSession().getAttribute("newRegistration", PortletSession.APPLICATION_SCOPE);
			
			if (newRegistration == null) {
				include(editJSP, renderRequest, renderResponse);	
			} else {
				renderRequest.setAttribute("newRegistration", newRegistration);
				include(submitVerify, renderRequest, renderResponse);
			}
			
		}

	}

	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		// Process data here
		PortletSession session = actionRequest.getPortletSession(true);
		String editRegistrationIdStr = actionRequest.getParameter("editRegistrationId");

		String action = "";
		List<String> errors = checkFormForErrors(actionRequest);

		if (editRegistrationIdStr == null) {
			action = "ADD";
			_log.info("request to add new registration");
			try {
				checkCaptcha(actionRequest);
			} catch (Exception e) {
				_log.info("Captcha exception: " + e.getMessage());
				errors.add("Invalid Captcha text, please try again");
			}
		} else {
			action = "EDIT";
			_log.info("edit registration id " + editRegistrationIdStr);
		}

		ConferenceFormBean formBean = extractRequestParameters(actionRequest);

		

		if (errors.size() > 0) {

			actionRequest.setAttribute("errors", errors);
			actionRequest.setAttribute("formBean", formBean);
		} else {

			if (action.equals("ADD")) {
				try {
					addNewRegistration(actionResponse, session, formBean);
				} catch (RegistrationProcessingException e) {
					errors.add(e.getMessage());
				}
			} else if (action.equals("EDIT")) {
				editRegistration(actionRequest, formBean);
			}

		} // end if ! errors

	}
	private void checkCaptcha(PortletRequest request) throws Exception {
        String enteredCaptchaText = ParamUtil.getString(request, "captchaText");

        PortletSession session = request.getPortletSession();
        String captchaText = getCaptchaValueFromSession(session);
        if (Validator.isNull(captchaText)) {
            throw new Exception("Internal Error! Captcha text not found in session");
        }
        if (!captchaText.equals(enteredCaptchaText.trim())) {
        	_log.info("Captcha expected: " + captchaText + " Entered: " + enteredCaptchaText);
            throw new Exception("Invalid captcha text. Please reenter.");
        }
    }

    private String getCaptchaValueFromSession(PortletSession session) {
        Enumeration<String> atNames = session.getAttributeNames();
        while (atNames.hasMoreElements()) {
            String name = atNames.nextElement();
            if (name.contains("CAPTCHA_TEXT")) {
                return (String) session.getAttribute(name);
            }
        }
        return null;
    }
	private List<String> checkFormForErrors(ActionRequest actionRequest) {
		List<String> errors = new ArrayList<String>();
		String firstName = actionRequest.getParameter("firstName");
		String lastName = actionRequest.getParameter("lastName");
		String streetAddress = actionRequest.getParameter("streetAddress");
		String city = actionRequest.getParameter("city");
		String state = actionRequest.getParameter("state");
		String postcode = actionRequest.getParameter("postcode");
		String country = actionRequest.getParameter("country");
		String email = actionRequest.getParameter("email");
		String phone = actionRequest.getParameter("phone");
		String institution = actionRequest.getParameter("institution");
		String registrationRate = actionRequest.getParameter("registrationRate");

		if ((firstName == null) || firstName.isEmpty()
				|| (lastName == null) || lastName.isEmpty()
				|| (streetAddress == null) || streetAddress.isEmpty()
				|| (city == null) || city.isEmpty() || (state == null)
				|| state.isEmpty() || (postcode == null) || postcode.isEmpty()
				|| (country == null) || country.isEmpty() || (email == null)
				|| email.isEmpty() || (phone == null) || phone.isEmpty()
				|| (institution == null) || institution.isEmpty()
				|| (registrationRate == null) || registrationRate.isEmpty()) {

			errors.add("All fields are required.");
		}


		if ((email == null) || email.isEmpty()) {
			errors.add("Please include a valid email address.");
		}
		return errors;
	}

	private void editRegistration(ActionRequest actionRequest,
			ConferenceFormBean crb) {

		_log.info("editing.");

		long editRegistrationId = Long.parseLong(actionRequest
				.getParameter("editRegistrationId"));
		ConferenceRegistration editRegistration = conferenceRegistrationDao.getById(editRegistrationId);
		
		String paypalStatus = actionRequest.getParameter("paypalStatus");
		String removeFlag = actionRequest.getParameter("removeFlag");
		List<String> messages = new ArrayList<String>();
		Date now = new Date();

		try {
			BeanUtils.copyProperties(editRegistration, crb);
		} catch (IllegalAccessException e) {
			_log.error("Error copying properties", e);
		} catch (InvocationTargetException e) {
			_log.error("Error copying properties", e);
		}

		editRegistration.setPaypalStatus(paypalStatus);
		editRegistration.setUpdateDate(now);
		_log.info("checking remove flag.");
		if (removeFlag.equals("Y")) {
			editRegistration.setIsActive(false);
			messages.add("Registration record for " + editRegistration.getFirstName() + " "
					+ editRegistration.getLastName() + " has been deactivated.");
		}
		conferenceRegistrationDao.save(editRegistration);
		_log.info("updated registration.");
		messages.add("Registration record for " + editRegistration.getFirstName() + " "
				+ editRegistration.getLastName() + " has been updated.");
		actionRequest.setAttribute("messages", messages);

	}

	
	private void addNewRegistration(ActionResponse actionResponse,
			PortletSession session, ConferenceFormBean crb) throws RegistrationProcessingException {

		ConferenceRegistration newRegistration = new ConferenceRegistration();
		try {
			BeanUtils.copyProperties(newRegistration, crb);
		} catch (Exception e1) {
			_log.error("Error copying properties", e1);
		}

		// calculate Registration amount
		String paypalItemName = "Australian Coral Reef Society Conference 2011";
		int registrationAmount = 0;

		String registrationRate = newRegistration.getRegistrationRate();
		
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
			_log.error("Invalid registration rate: '" + registrationRate + "'");
			throw new RegistrationProcessingException("Can't calculate registration rate");
		}
		if (newRegistration.getStudentMentoringDay()) {
			registrationAmount -= 50;
			paypalItemName += " + Student Mentoring Day";
		}
		
		if (newRegistration.getCoralFinderWorkshop()) {
			registrationAmount += 10;
			paypalItemName += " + Coral Finder Workshop";
		}
		
		if (newRegistration.getAdditionalTicketsWelcome() > 0) {
			registrationAmount += newRegistration.getAdditionalTicketsWelcome() * 10;
			paypalItemName += " + " + newRegistration.getAdditionalTicketsWelcome() + " Welcome Event Tickets";
		}
		
		if (newRegistration.getAdditionalTicketsDinner() > 0) {
			registrationAmount += newRegistration.getAdditionalTicketsDinner() * 10;
			paypalItemName += " + " + newRegistration.getAdditionalTicketsDinner() + " Dinner Tickets";
		}

		
		// save registration details


		newRegistration.setPaypalRef("");
		newRegistration.setPaypalStatus("Unverified");
		newRegistration.setRegistrationAmount(registrationAmount);
		newRegistration.setIsActive(true);
		newRegistration.setUpdateDate(newRegistration.getRegistrationDate());

		conferenceRegistrationDao.save(newRegistration);

		// email stuff out
//		String approvalEmail1 = ACRSApplication.getConfiguration()
//				.getApprovalEmail1();
//		String approvalEmail2 = ACRSApplication.getConfiguration()
//				.getApprovalEmail2();
//
//		String approvalMessage = "Hi ACRS, \n\nPlease find below details of an application for membership that has been submitted. \n\nKind Regards, \nThe ACRS Website\n\n";
//		String applicantDetail = "\n\tName:\t\t\t\t" + newRegistration.getTitle()
//				+ " " + newRegistration.getFirstName() + " "
//				+ newRegistration.getLastName() + "\n\tAddress:\t\t\t"
//				+ newRegistration.getStreetAddress() + ", " + newRegistration.getCity()
//				+ " " + newRegistration.getState() + " " + newRegistration.getPostcode()
//				+ "\n\tEmail:\t\t\t" + newRegistration.getEmail()
//				+ "\n\tPhone:\t\t\t" + newRegistration.getPhone()
//				+ "\n\tInstitution:\t\t" + newRegistration.getInstitution();

//		try {
//			Emailer.sendEmail(approvalEmail1, "no-reply@acrs.org",
//					"New ACRS Membership", approvalMessage + applicantDetail);
//			Emailer.sendEmail(approvalEmail2, "no-reply@acrs.org",
//					"New ACRS Membership", approvalMessage + applicantDetail);
//
//		} catch (MessagingException e) {
//			_log.fatal("Could not send email.");
//		}

		actionResponse.setRenderParameter("newRegistrationId",
				String.valueOf(newRegistration.getId()));
		session.setAttribute("newRegistration", newRegistration,
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
			_log.error("Error loading parameters: " + map, e1);
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
	
    /** Serve Resource used for getting captcha
     *
     */
    @Override
    public void serveResource(ResourceRequest resourceRequest,
                             ResourceResponse resourceResponse) throws IOException, PortletException {
    	String resourceID = resourceRequest.getResourceID();
    	if ("captcha".equals(resourceID)) {
    		serveResourceCaptcha(resourceRequest, resourceResponse);
    	} else if ("spreadsheet".equals(resourceID)) {
    		serveResourceSpreadsheet(resourceRequest, resourceResponse);
    	}
    }
    
    public void serveResourceCaptcha(ResourceRequest resourceRequest,
                             ResourceResponse resourceResponse) throws IOException, PortletException {
        try {
            com.liferay.portal.kernel.captcha.CaptchaUtil.serveImage(resourceRequest, resourceResponse);
        } catch (Exception e) {
            _log.error(e);
        }
    }
    
	public void serveResourceSpreadsheet(ResourceRequest req, ResourceResponse res)
			throws PortletException, IOException {

		// create an excel file containing the registration list for the user to
		// download
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet("ACRS Conference Registrations 2011 "
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
		headings.add("Submitting Abstract");
		headings.add("Registration Rate");
		headings.add("Student Mentoring Day");
		headings.add("Coral Finder Workshop");
		headings.add("Welcome Tickets");
		headings.add("Dinner Tickets");
		headings.add("Registration Amount");
		headings.add("Registration Date");
		headings.add("Updated Date");
		headings.add("Paypal Status");
		headings.add("Paypal Confirmation Details");

		// write to sheet
		for (String h : headings) {
			c = th.createCell(headings.indexOf(h));
			HSSFRichTextString rts = new HSSFRichTextString(h);
			c.setCellValue(rts);
			c.setCellStyle(thStyle);
		}

		// add registrations
		List<ConferenceRegistration> allRegistrations = conferenceRegistrationDao.getAll();
		for (ConferenceRegistration registration : allRegistrations) {

			ArrayList<String> a = new ArrayList<String>();
			a.add(registration.getTitle());
			a.add(registration.getFirstName());
			a.add(registration.getLastName());
			a.add(registration.getStreetAddress());
			a.add(registration.getCity());
			a.add(registration.getState());
			a.add(registration.getPostcode());
			a.add(registration.getCountry());
			a.add(registration.getEmail());
			a.add(registration.getPhone());
			a.add(registration.getInstitution());
			a.add(registration.getSubmittingAbstract() ? "Y" : "N");
			a.add(registration.getRegistrationRate());
			a.add(registration.getStudentMentoringDay() ? "Y" : "N");
			a.add(registration.getCoralFinderWorkshop() ? "Y" : "N");
			a.add(registration.getAdditionalTicketsWelcome().toString());
			a.add(registration.getAdditionalTicketsDinner().toString());
			a.add(registration.getRegistrationAmount().toString());
			a.add(registration.getRegistrationDate().toString());
			a.add(registration.getUpdateDate().toString());
			a.add(registration.getPaypalStatus());
			a.add(registration.getPaypalRef());

			r = s.createRow(allRegistrations.indexOf(registration) + 1);
			Iterator<String> i = a.iterator();
			int cellNum = 0;

			while (i.hasNext()) {
				c = r.createCell(cellNum);
				HSSFRichTextString rts = new HSSFRichTextString(i.next());
				c.setCellValue(rts);
				s.autoSizeColumn((short) cellNum);
				cellNum++;
			}
		}

		// write workbook out to file
		FileOutputStream fos = new FileOutputStream("ConferenceRegistrations2011.xls");
		wb.write(fos);
		fos.flush();
		fos.close();

		File file = new File("ConferenceRegistrations2011.xls");
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

}
