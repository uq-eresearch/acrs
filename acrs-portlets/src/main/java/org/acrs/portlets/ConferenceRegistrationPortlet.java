package org.acrs.portlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
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
import org.acrs.data.model.SystemClock;
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
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * Author: uqdayers Date: 19/4/2013
 * Author: alabri Date: 08/02/2011 Time: 4:26:58 PM
 */
public class ConferenceRegistrationPortlet extends GenericPortlet {
	private static Log _log = LogFactoryUtil
			.getLog(ConferenceRegistrationPortlet.class);
	protected String viewJSP;
	protected final String editJSP = "/WEB-INF/jsp/addeditconf.jsp";
	protected final String registrationsListJSP = "/WEB-INF/jsp/registrations.jsp";
	protected final String submitVerify = "/WEB-INF/jsp/submit.jsp";
	protected Date earlyBirdDate;

	protected ConferenceRegistrationDao conferenceRegistrationDao;

	public void init() throws PortletException {
		viewJSP = getInitParameter("confreg-jsp");
		conferenceRegistrationDao = ACRSApplication.getConfiguration()
				.getConferenceRegistrationDao();
		try {
			earlyBirdDate = new SimpleDateFormat("yyyy-MM-dd").parse("2015-05-28");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Standard portlet function for handling view requests. Determines whether
	 * the user is an admin, and if they are adding/editing/listing/paying,
	 * returning the appropriate view.
	 */
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		String cmd = ParamUtil.getString(renderRequest, "cmd");
		// Do all you views here

		// if the user is a liferay admin list the registration database in the
		// JSP.
		boolean isAdmin = renderRequest.isUserInRole("administrator");
		renderRequest.setAttribute("isAdmin", isAdmin);

		if (isAdmin) {
			if ("EDIT".equals(cmd)) {
				adminEditView(renderRequest, renderResponse);
			} else {
				adminListView(renderRequest, renderResponse);
			}
		} else {
			userView(renderRequest, renderResponse);
		}

	}

	/**
	 * Renders the view for a normal user, either the registration form for them
	 * to fill in, or the submitted form that shows their details and lets them
	 * continue to paypal to pay.
	 * 
	 * @param renderRequest
	 * @param renderResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	private void userView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		ConferenceRegistration newRegistration = (ConferenceRegistration) renderRequest
				.getPortletSession().getAttribute("newRegistration",
						PortletSession.APPLICATION_SCOPE);

		if (newRegistration == null) {
			Date today = new Date();
			checkIsEarlybird(renderRequest, today);
			
			include(editJSP, renderRequest, renderResponse);
		} else {
			renderRequest.setAttribute("newRegistration", newRegistration);
			include(submitVerify, renderRequest, renderResponse);
		}
	}

	/**
	 * @param renderRequest
	 * @param registrationDate
	 */
	protected void checkIsEarlybird(RenderRequest renderRequest, Date registrationDate) {
		if (registrationDate.before(earlyBirdDate)) {
			renderRequest.setAttribute("isEarlybird", true);
		} else {
			renderRequest.setAttribute("isEarlybird", false);
		}
	}

	/**
	 * Renders the admin list view, containing all the registrations as a table,
	 * and allowing downloading in excel format.
	 * 
	 * @param renderRequest
	 * @param renderResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	private void adminListView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		List<ConferenceRegistration> allRegistrations = conferenceRegistrationDao
				.getAll();
		renderRequest.setAttribute("allRegistrations", allRegistrations);

		include(registrationsListJSP, renderRequest, renderResponse);
	}

	/**
	 * Renders the admin edit view, for editing any registration details after a
	 * user has registered.
	 * 
	 * @param renderRequest
	 * @param renderResponse
	 * @throws IOException
	 * @throws PortletException
	 * @throws RegistrationProcessingException
	 */
	private void adminEditView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		long registrationId = ParamUtil
				.getLong(renderRequest, "registrationId");
		ConferenceRegistration editRegistration = conferenceRegistrationDao
				.getById(registrationId);

		ConferenceFormBean formBean = new ConferenceFormBean(editRegistration);

		renderRequest.setAttribute("formBean", formBean);
		renderRequest.setAttribute("editRegistration", editRegistration);

		checkIsEarlybird(renderRequest, editRegistration.getRegistrationDate());

		include(editJSP, renderRequest, renderResponse);
	}

	/**
	 * Standard portlet function, for processing any form submissions. Handles
	 * add requests from general users and edit requests from administrators.
	 * 
	 * Checks for errors in the submitted information, and returns an error to
	 * the user if necessary, allowing them to fix the error. Also checks the
	 * Captcha text if it is a new registration.
	 */
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		// Process data here
		PortletSession session = actionRequest.getPortletSession(true);
		String editRegistrationIdStr = actionRequest
				.getParameter("editRegistrationId");

		String action = "";
		List<String> errors = checkFormForErrors(actionRequest);

		if (editRegistrationIdStr == null) {
			action = "ADD";
			_log.info("request to add new registration");
			try {
				checkCaptcha(actionRequest);
			} catch (RegistrationProcessingException e) {
				_log.info("Captcha exception: " + e.getMessage());
				errors.add("Invalid Captcha text, please try again");
			}
		} else {
			action = "EDIT";
			_log.info("edit registration id " + editRegistrationIdStr);
		}

		ConferenceFormBean formBean = new ConferenceFormBean(actionRequest);

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

	/**
	 * Check the Liferay standard captcha text.
	 * 
	 * @param request
	 * @throws RegistrationProcessingException
	 */
	private void checkCaptcha(PortletRequest request)
			throws RegistrationProcessingException {
		String enteredCaptchaText = ParamUtil.getString(request, "captchaText");

		PortletSession session = request.getPortletSession();
		String captchaText = getCaptchaValueFromSession(session);
		if (Validator.isNull(captchaText)) {
			throw new RegistrationProcessingException(
					"Internal Error! Captcha text not found in session");
		}
		if (!captchaText.equals(enteredCaptchaText.trim())) {
			_log.info("Captcha expected: " + captchaText + " Entered: "
					+ enteredCaptchaText);
			throw new RegistrationProcessingException(
					"Invalid captcha text. Please reenter.");
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

	/**
	 * Check that all required fields have been filled. Operates directly on the
	 * actionRequest, instead of on the FormBean like it should. Returns a list
	 * of error strings, or an empty list if no errors.
	 * 
	 * @param actionRequest
	 * @return empty list if no errors, list of errors strings if errors.
	 */
	private List<String> checkFormForErrors(ActionRequest actionRequest) {
		List<String> errors = new ArrayList<String>();
		String firstName = actionRequest.getParameter("firstName");
		String lastName = actionRequest.getParameter("lastName");
		String email = actionRequest.getParameter("email");
		String registrationRate = actionRequest
				.getParameter("registrationRate");

		if ((firstName == null) || firstName.isEmpty() || (lastName == null)
				|| lastName.isEmpty()
				|| (email == null) || email.isEmpty()
				|| (registrationRate == null)
				|| registrationRate.isEmpty()) {

			errors.add("Please fill all required fields.");
		}

		if ((email == null) || email.isEmpty()) {
			errors.add("Please include a valid email address.");
		}
		return errors;
	}

	/**
	 * Processes an admin request to edit a registration. Includes the ability
	 * to remove a registration and update the paypal status.
	 * 
	 * @param actionRequest
	 * @param crb
	 */
	private void editRegistration(ActionRequest actionRequest,
			ConferenceFormBean crb) {

		_log.info("editing.");

		long editRegistrationId = Long.parseLong(actionRequest
				.getParameter("editRegistrationId"));
		ConferenceRegistration editRegistration = conferenceRegistrationDao
				.getById(editRegistrationId);

		String paypalStatus = actionRequest.getParameter("paypalStatus");
		String removeFlag = actionRequest.getParameter("removeFlag");
		List<String> messages = new ArrayList<String>();
		Date now = new Date();

		try {
			BeanUtils.copyProperties(editRegistration, crb);
		} catch (Exception e) {
			_log.error("Error copying properties", e);
		}

		editRegistration.setPaypalStatus(paypalStatus);
		editRegistration.setUpdateDate(now);
		_log.info("checking remove flag.");
		if (removeFlag.equals("Y")) {
			editRegistration.setIsActive(false);
			messages.add("Registration record for "
					+ editRegistration.getFirstName() + " "
					+ editRegistration.getLastName() + " has been deactivated.");
		}
		conferenceRegistrationDao.save(editRegistration);
		_log.info("updated registration.");
		messages.add("Registration record for "
				+ editRegistration.getFirstName() + " "
				+ editRegistration.getLastName() + " has been updated.");
		actionRequest.setAttribute("messages", messages);

	}

	/**
	 * Add a new registration from a user submitted request. The input should
	 * already have been checked for errors.
	 * 
	 * @param actionResponse
	 * @param session
	 * @param crb
	 * @throws RegistrationProcessingException
	 */
	private void addNewRegistration(ActionResponse actionResponse,
			PortletSession session, ConferenceFormBean crb)
			throws RegistrationProcessingException {

		ConferenceRegistration newRegistration = new ConferenceRegistration(crb, new SystemClock());

		// calculate and save registration details

		newRegistration.calculateRegistration();

		newRegistration.setPaypalRef("");
		newRegistration.setPaypalStatus("Unverified");
		newRegistration.setIsActive(true);
		newRegistration.setUpdateDate(newRegistration.getRegistrationDate());

		conferenceRegistrationDao.save(newRegistration);

		// email notifications
		sendEmailNotifications(newRegistration);

		actionResponse.setRenderParameter("newRegistrationId",
				String.valueOf(newRegistration.getId()));
		session.setAttribute("newRegistration", newRegistration,
				PortletSession.APPLICATION_SCOPE);
		session.setAttribute("paypalItemName",
				newRegistration.getPaypalItemName(),
				PortletSession.APPLICATION_SCOPE);

	}

	/**
	 * Send email notification to the configured addresses when a new
	 * registration is recorded
	 * 
	 * @param newRegistration
	 *            The new conference registration, that has been saved in the
	 *            database
	 */
	private void sendEmailNotifications(ConferenceRegistration newRegistration) {
		String approvalEmail1 = ACRSApplication.getConfiguration()
				.getApprovalEmail1();
		String approvalEmail2 = ACRSApplication.getConfiguration()
				.getApprovalEmail2();

		String approvalMessage = "Hi ACRS, \n\nPlease find below details of 2015 Conference Registration that has been submitted. \n\nKind Regards, \nThe ACRS Website\n\n";
		String applicantDetail = "\n\tName:\t\t\t\t"
				+ newRegistration.getTitle() + " "
				+ newRegistration.getFirstName() + " "
				+ newRegistration.getLastName() + "\n\tEmail:\t\t\t"
				+ newRegistration.getEmail() + "\n\tInstitution:\t\t"
				+ newRegistration.getInstitution();

		try {
			Emailer.sendEmail(approvalEmail1, "no-reply@acrs.org",
					"New ACRS Conference Registration", approvalMessage + applicantDetail);
			Emailer.sendEmail(approvalEmail2, "no-reply@acrs.org",
					"New ACRS Conference Registration", approvalMessage + applicantDetail);

		} catch (MessagingException e) {
			_log.fatal("Could not send email.");
		}
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

	/**
	 * Standard portlet function for serving resources, chooses between a
	 * spreadsheet or a captcha image
	 */
	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		String resourceID = resourceRequest.getResourceID();
		if ("captcha".equals(resourceID)) {
			serveResourceCaptcha(resourceRequest, resourceResponse);
		} else if ("spreadsheet".equals(resourceID)) {
			serveResourceSpreadsheet(resourceResponse);
		}
	}

	/**
	 * Serve Resource used for getting captcha, provided by Liferay
	 */
	public void serveResourceCaptcha(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {
		try {
			com.liferay.portal.kernel.captcha.CaptchaUtil.serveImage(
					resourceRequest, resourceResponse);
		} catch (Exception e) {
			_log.error(e);
		}
	}

	/**
	 * Create and serve an excel spreadsheet containing all conference
	 * registrations.
	 * 
	 * @param res
	 * @throws PortletException
	 * @throws IOException
	 */
	public void serveResourceSpreadsheet(ResourceResponse res)
			throws PortletException, IOException {

		// create an excel file containing the registration list for the user to
		// download
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet("ACRS Conference Registrations 2015 "
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
		headings.add("Email");
		headings.add("Institution");
                headings.add("Phone Number");
		headings.add("Submitting Abstract");
		headings.add("Registration Rate");
		headings.add("Student Mentoring Discount");
                headings.add("Daydream Island Arrival Date");
                headings.add("Daydream Island Departure Date");
                headings.add("Boat Transfers");
		headings.add("Special Food Requirements");
                headings.add("Special Wheelchair Requirements");
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
		List<ConferenceRegistration> allRegistrations = conferenceRegistrationDao
				.getAll();
		for (ConferenceRegistration registration : allRegistrations) {

			ArrayList<String> a = new ArrayList<String>();
			a.add(registration.getTitle());
			a.add(registration.getFirstName());
			a.add(registration.getLastName());
			a.add(registration.getEmail());
			a.add(registration.getInstitution());
                        a.add(registration.getPhone());
			a.add(registration.getSubmittingAbstract() ? "Y" : "N");
			a.add(registration.getRegistrationRate());
			a.add(registration.getStudentMentoringDiscount() ? "Y" : "N");
                        a.add(registration.getArrivalDateDaydreamIsland());
                        a.add(registration.getDepartureDateDaydreamIsland());
                        a.add(registration.getBoatTransfers());
			a.add(registration.getSpecialFoodRequirements());
                        a.add(registration.getSpecialWheelchairRequirements());
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
				cellNum++;
			}
		}
        
        // Auto-resize all the columns
        for (int column = 0; column < headings.size(); column++) {
        	 s.autoSizeColumn((short)column);
        }


		res.setContentType("application/vnd.ms-excel");
        res.addProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ConferenceRegistrations2015.xls\"");
        res.setProperty(ResourceResponse.EXPIRATION_CACHE, "0");
        wb.write(res.getPortletOutputStream());
        res.getPortletOutputStream().close();

	}

}
