package org.acrs.portlets;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import org.acrs.app.ACRSApplication;
import org.acrs.data.access.MemberDao;
import org.acrs.data.model.Member;
import org.acrs.util.Emailer;
import org.apache.poi.hssf.usermodel.*;

import javax.mail.MessagingException;
import javax.portlet.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:26:58 PM
 */
public class MembersPortlet extends GenericPortlet {
    private static Log _log = LogFactoryUtil.getLog(MembersPortlet.class);
    protected String viewJSP;
    protected MemberDao membersDao;

    public void init() throws PortletException {
        viewJSP = getInitParameter("member-jsp");
        membersDao = ACRSApplication.getConfiguration().getUserDao();
    }

    public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

        //Do all you views here

        // if the user is a liferay admin list the membership database in the JSP.
        boolean isAdmin = renderRequest.isUserInRole("administrator");
        renderRequest.setAttribute("isAdmin", isAdmin);

        List<Member> allMembers = membersDao.getAll();
        renderRequest.setAttribute("allMembers", allMembers);
        renderRequest.setAttribute("membersDao", membersDao);

        include(viewJSP, renderRequest, renderResponse);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {

        //Process data here
        PortletSession session = actionRequest.getPortletSession(true);
        String editMemberIdStr = actionRequest.getParameter("editMemberId");
        String action = "";
        List<String> errors = new ArrayList<String>();

        if (editMemberIdStr == null) {
            action = "ADD";
            _log.info("request to add new member");
            
			try {
				checkCaptcha(actionRequest);
			} catch (RegistrationProcessingException e) {
				_log.info("Captcha exception: " + e.getMessage());
				errors.add("Invalid Captcha text, please try again");
			}
            
        } else {
            action = "EDIT";
            _log.info("edit member id " + editMemberIdStr);
        }
        
        String title = actionRequest.getParameter("title");
        String firstName = actionRequest.getParameter("firstName");
        String lastName = actionRequest.getParameter("lastName");
        String streetAddress = actionRequest.getParameter("streetAddress") + " " + actionRequest.getParameter("streetAddress2");
        String city = actionRequest.getParameter("city");
        String state = actionRequest.getParameter("state");
        String postcode = actionRequest.getParameter("postcode");
        String country = actionRequest.getParameter("country");
        String email = actionRequest.getParameter("email");
        String phone = actionRequest.getParameter("phone");
        String institution = actionRequest.getParameter("institution");
        String researchInterest = actionRequest.getParameter("researchInterest");
        String newsletterPref = actionRequest.getParameter("newsletterPref") + ", " + actionRequest.getParameter("newsletterPref2");
        String membershipType = actionRequest.getParameter("membershipType");
        String renewalFlag = actionRequest.getParameter("renewalFlag");
        String acrsEmailListFlag = actionRequest.getParameter("acrsEmailListFlag");

        // tidy up nulls
        if ((acrsEmailListFlag == null) || (acrsEmailListFlag.isEmpty())) {
            acrsEmailListFlag = "N";
        }
        if ((renewalFlag == null) || (renewalFlag.isEmpty())){
            renewalFlag = "N";
        }

        if ((firstName == null) || firstName.isEmpty()
                || (streetAddress == null) || streetAddress.isEmpty()
                || (city == null) || city.isEmpty()
                || (state == null) || state.isEmpty()
                || (postcode == null) || postcode.isEmpty()
                || (country == null) || country.isEmpty()
                || (email == null) || email.isEmpty()
                || (phone == null) || phone.isEmpty()
                || (institution == null) || institution.isEmpty()
                || (researchInterest == null) || researchInterest.isEmpty()
                || (membershipType == null) || membershipType.isEmpty()) {

            errors.add("All fields are required.");
        }
        
        if ((firstName.contains("<")) || (firstName.contains(">"))
            	||	(lastName.contains("<")) || (lastName.contains(">"))
            	||	(streetAddress.contains("<")) || (streetAddress.contains(">"))
            	||	(city.contains("<")) || (city.contains(">"))
            	||	(state.contains("<")) || (state.contains(">"))
            	||	(email.contains("<")) || (email.contains(">"))
            	||	(phone.contains("<")) || (phone.contains(">"))
            	||	(institution.contains("<")) || (institution.contains(">"))
            	||	(researchInterest.contains("<")) || (researchInterest.contains(">")) )
        {
        	errors.add("Please, remove these characters from the form before continuing: < >");
        }
        
        if (!(phone == null || phone.isEmpty())) {
            
        	String strippedPhoneNumber = phone.replaceAll("[a-zA-Z]", "");
        	if (strippedPhoneNumber.length() < 8 ) {
        		errors.add("Invalid phone number format.");
        	}
        }
        
        
        if ((email == null) || email.isEmpty()) {
            errors.add("Please include a valid email address.");
        }


        if (errors.size() > 0) {

            actionRequest.setAttribute("errors", errors);
        } else {

            if (action.equals("ADD")) {

                Member newMember = new Member();
        		Double membershipAmount = 0.00;
                String paypalItemName = "Australian Coral Reef Society ";

                // Establish membership registration year
                Calendar today = Calendar.getInstance();
                int thisYear = today.get(Calendar.YEAR);
                int nextYear = thisYear + 1;
                int membershipYear = thisYear;
                
                // if date is between November 1 and December 31, registration is for next year
                Calendar preYearStart = Calendar.getInstance();
                preYearStart.set(thisYear, Calendar.NOVEMBER, 1, 0, 0, 0);
                Calendar preYearEnd = Calendar.getInstance();
                preYearEnd.set(nextYear, Calendar.JANUARY, 1, 0, 0, 0);
                
                if ((today.after(preYearStart)) && (today.before(preYearEnd))) {
                	membershipYear = nextYear;
                }
                
                // if date is between November 01 and Mar 01, a discount applies
                Calendar startDiscountDt = Calendar.getInstance();
                startDiscountDt.set(membershipYear-1, Calendar.NOVEMBER, 1, 0, 0, 0);
                Calendar endDiscountDt = Calendar.getInstance();
                endDiscountDt.set(membershipYear, Calendar.MARCH, 1, 0, 0, 0);
                
                if (membershipType.equals("Full")) {
                    membershipAmount = 50.00;
                    paypalItemName = paypalItemName + "Full Membership for " + Integer.toString(membershipYear);
                } else if (membershipType.equals("Student")) {
                    membershipAmount = 30.00;
                    paypalItemName = paypalItemName + "Student Membership for " + Integer.toString(membershipYear);
                } else if (membershipType.equals("FiveYear")) {
                    membershipAmount = 200.00;
                    paypalItemName = paypalItemName + "Full 5 Year Membership from " + Integer.toString(membershipYear);
                } else if (membershipType.equals("Test")) {
                    membershipAmount = 5.00;
                    paypalItemName = paypalItemName + "Test Membership from " + Integer.toString(membershipYear);
                }

                if (!(membershipType.equals("FiveYear"))
                        && (today.after(startDiscountDt))
                        && (today.before(endDiscountDt))) {
                    membershipAmount = membershipAmount - 10.00;
                    paypalItemName = paypalItemName + " (with early discount)";
                }
                
                
                // save member details
                newMember.setTitle(title);
                newMember.setFirstName(firstName);
                newMember.setLastName(lastName);
                newMember.setStreetAddress(streetAddress);
                newMember.setCity(city);
                newMember.setState(state);
                newMember.setPostcode(postcode);
                newMember.setCountry(country);
                newMember.setEmail(email);
                newMember.setPhone(phone);
                newMember.setInstitution(institution);
                newMember.setResearchInterest(researchInterest);
                newMember.setNewsletterPref(newsletterPref);
                newMember.setMembershipType(membershipType);
                newMember.setMembershipAmount(membershipAmount);
                newMember.setRenewalFlag(renewalFlag);
                newMember.setAcrsEmailListFlag(acrsEmailListFlag);
                newMember.setPaypalRef("");
                newMember.setPaypalStatus("Unverified");
                newMember.setIsActive(true);
                newMember.setUpdateDate(newMember.getRegistrationDate());

                membersDao.save(newMember);

                // email stuff out
                String approvalEmail1 = ACRSApplication.getConfiguration().getApprovalEmail1();
                String approvalEmail2 = ACRSApplication.getConfiguration().getApprovalEmail2();
                String emailListCoordEmail = ACRSApplication.getConfiguration().getEmailListCoordEmail();

                String approvalMessage = "Hi ACRS, \n\nPlease find below details of an application for membership that has been submitted. \n\nKind Regards, \nThe ACRS Website\n\n";
                String emailListMessage = "Hi, \n\nThe following membership applicant indicated a desire to subscribe to the ACRS Mailing List. \n\nKind Regards, \nThe ACRS Website\n\n";
                String applicantDetail = "\n\tName:\t\t\t\t" + title + " " + firstName + " " + lastName
                        + "\n\tAddress:\t\t\t" + streetAddress + ", " + city + " " + state + " " + postcode
                        + "\n\tEmail:\t\t\t" + email
                        + "\n\tPhone:\t\t\t" + phone
                        + "\n\tInstitution:\t\t" + institution
                        + "\n\tResearch Interest:\t" + researchInterest
                        + "\n\tNewsletter Preference:\t" + newsletterPref
                        + "\n\tMembership Type: \t\t" + membershipType
                        + "\n\tMembership Amount: \t" + membershipAmount + "0";

                try {
                    Emailer.sendEmail(approvalEmail1, "no-reply@acrs.org", "New ACRS Membership", approvalMessage + applicantDetail);
                    Emailer.sendEmail(approvalEmail2, "no-reply@acrs.org", "New ACRS Membership", approvalMessage + applicantDetail);

                    if (acrsEmailListFlag.equals("Y")) {
                        Emailer.sendEmail(emailListCoordEmail, "no-reply@acrs.org", "New ACRS Mail List Subscribe Request", emailListMessage + applicantDetail);
                    }

                } catch (MessagingException e) {
                    _log.fatal("Could not send email.");
                }

                actionResponse.setRenderParameter("newMemberId", String.valueOf(newMember.getId()));
                session.setAttribute("newMember", newMember, PortletSession.APPLICATION_SCOPE);
                session.setAttribute("paypalItemName", paypalItemName, PortletSession.APPLICATION_SCOPE);
            } // end if ADD

            else if (action.equals("EDIT")) {
            	_log.info("editing.");
                //String memberId = ParamUtil.getString(actionRequest, "editMemberId");
                //Member editMember = new Member();
                long editMemberId = Long.parseLong(editMemberIdStr);
                Member editMember = membersDao.getById(editMemberId);
                Double membershipAmount = Double.parseDouble(actionRequest.getParameter("membershipAmount"));
                String paypalStatus = actionRequest.getParameter("paypalStatus");
                String removeFlag = actionRequest.getParameter("removeFlag");
                List<String> messages = new ArrayList<String>();
                Date now = new Date();
                
                editMember.setTitle(title);
                editMember.setFirstName(firstName);
                editMember.setLastName(lastName);
                editMember.setStreetAddress(streetAddress);
                editMember.setCity(city);
                editMember.setState(state);
                editMember.setPostcode(postcode);
                editMember.setCountry(country);
                editMember.setEmail(email);
                editMember.setPhone(phone);
                editMember.setInstitution(institution);
                editMember.setResearchInterest(researchInterest);
                editMember.setNewsletterPref(newsletterPref);
                editMember.setMembershipType(membershipType);
                editMember.setMembershipAmount(membershipAmount);
                editMember.setRenewalFlag(renewalFlag);
                editMember.setAcrsEmailListFlag(acrsEmailListFlag);
                editMember.setPaypalStatus(paypalStatus);
                editMember.setUpdateDate(now);
                _log.info("checking remove flag.");
                if (removeFlag.equals("Y")) {
                	editMember.setIsActive(false);
                	messages.add("Member record for " + editMember.getFirstName() + " " + editMember.getLastName()
                            + " has been deactivated.");
                }
                membersDao.save(editMember);
                _log.info("updated memeber.");
                messages.add("Member record for " + editMember.getFirstName() + " " + editMember.getLastName()
                        + " has been updated.");
                actionRequest.setAttribute("messages", messages);
                
                
            }

            
        } // end if ! errors
      
    }

    protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

        PortletContext portletContext = getPortletContext();
        PortletRequestDispatcher portletRequestDispatcher = portletContext.getRequestDispatcher(path);
        if (portletRequestDispatcher == null) {
            _log.error(path + " is not a valid include");
        } else {
            try {
                portletRequestDispatcher.include(renderRequest, renderResponse);
            } catch (Exception e) {
                _log.error(e, e);
                portletRequestDispatcher = portletContext.getRequestDispatcher("/error.jsp");

                if (portletRequestDispatcher == null) {
                    _log.error("/error.jsp is not a valid include");
                } else {
                    portletRequestDispatcher.include(renderRequest, renderResponse);
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
    
    public void serveResourceSpreadsheet(ResourceResponse res) throws PortletException, IOException {

        // create an excel file containing the member list for the user to download
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet s = wb.createSheet("ACRS Member Database " + sdf.format(new Date()));
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
        List<Member> allMembers = membersDao.getAll();
        for (Member member : allMembers) {

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
            	HSSFRichTextString rts = new HSSFRichTextString((String)i.next());
                c.setCellValue(rts);
                cellNum++;
            }

        }
        
        // Auto-resize all the columns
        for (int column = 0; column < headings.size(); column++) {
        	 s.autoSizeColumn((short)column);
        }

        res.setContentType("application/vnd.ms-excel");
        res.addProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"MemberList.xls\"");
        res.setProperty(ResourceResponse.EXPIRATION_CACHE, "0");
        wb.write(res.getPortletOutputStream());
        res.getPortletOutputStream().close();
        
/*
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
*/

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


}
