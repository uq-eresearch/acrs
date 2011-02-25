package org.acrs.portlets;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.acrs.app.ACRSApplication;
import org.acrs.data.access.MemberDao;
import org.acrs.data.model.Member;


import javax.ccpp.SetAttribute;
import javax.portlet.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import org.acrs.util.Emailer;
import javax.mail.MessagingException;

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

    	// form is the default non-admin view
    	String cmd = "_FORM";
    	renderRequest.setAttribute("cmd", cmd);
    	    	
    	List<Member> allMembers = membersDao.getAll();
    	renderRequest.setAttribute("allMembers", allMembers);
    	include(viewJSP, renderRequest, renderResponse);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {
    	
    	//Process data here
    	PortletSession session = actionRequest.getPortletSession(true);
    	List<String> errors = new ArrayList<String>();
    	    		
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
    	String newsletterPref = actionRequest.getParameter("newsletterPref")+ ", " + actionRequest.getParameter("newsletterPref2");
    	String membershipType = actionRequest.getParameter("membershipType");
    	String renewalFlag = actionRequest.getParameter("renewalFlag");
    	String acrsEmailListFlag = actionRequest.getParameter("acrsEmailListFlag");
    	
    	if ( (firstName == null) || firstName.isEmpty()
    	  || (streetAddress == null) || streetAddress.isEmpty()
    	  || (city == null) || city.isEmpty()
    	  || (state == null)|| state.isEmpty()
    	  || (postcode == null)|| postcode.isEmpty()
    	  || (country == null)|| country.isEmpty()
    	  || (email == null)|| email.isEmpty()
    	  || (phone == null)|| phone.isEmpty()
    	  || (institution == null)|| institution.isEmpty()
    	  || (researchInterest == null)|| researchInterest.isEmpty()
    	  || (newsletterPref == null)|| newsletterPref.isEmpty()
    	  || (membershipType == null)|| membershipType.isEmpty() ) {
    			
    		 errors.add("All fields are required.");
    	}
    	
    	if ((email == null) || email.isEmpty()) {
   		 	errors.add("Please include a valid email address.");
    	}
    	else {
    		//check for pre-existing email
    		if (membersDao.getByEmail(email) != null)
    		{
    			errors.add("The email address <b>" + email + "</b> is already registered with us. Please use a different email address.");
    		}
    	}
	
	    	
    	if (errors.size() > 0) {
    		
            actionRequest.setAttribute("errors", errors);
        }
    	
    	else {
    		
    		Member newMember = new Member();
	    	
    		// calculate membership amount
	    	Double membershipAmount = 0.00;
	    	String paypalItemName = "Australian Coral Reef Society ";
	    	int thisYear = Calendar.getInstance().get(Calendar.YEAR);
	    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	    	Date startDiscountDate = new Date();
	    	Date endDiscountDate = new Date();
	    	
	    	if (membershipType.equals("Full")) {
	    		membershipAmount=50.00;
	    		paypalItemName = paypalItemName + "Full Membership for " + Integer.toString(thisYear);
	    	
	    	} else if (membershipType.equals("Student")) {
	    		membershipAmount=30.00;
	    		paypalItemName = paypalItemName + "Student Membership for " + Integer.toString(thisYear);
	    	} else if (membershipType.equals("FiveYear")) {
	    		membershipAmount=200.00;
	    		paypalItemName = paypalItemName + "Full 5 Year Membership from " + Integer.toString(thisYear);
	    	}
	    	
	    	try {
				startDiscountDate = sdf.parse("01-01-" + Integer.toString(thisYear));
				endDiscountDate = sdf.parse("01-03-" + Integer.toString(thisYear));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			
	    	if ( !(membershipType.equals("FiveYear")) 
	    	    && (newMember.getRegistrationDate().after(startDiscountDate))
	    	    && (newMember.getRegistrationDate().before(endDiscountDate)) ) {
	    		membershipAmount = membershipAmount - 10.00;
	    		paypalItemName = paypalItemName + " (with early discount)";
	    	}
    		
	    	// tidy up nulls 
	    	if (acrsEmailListFlag == null) {acrsEmailListFlag = "N";}
	    	if (renewalFlag == null) {renewalFlag = "N";}
	    	
	    	
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
	    	
	    	membersDao.save(newMember);
	    	
	    	// email stuff out
	    	String approvalEmail1="peggy.newman@uq.edu.au";
	    	String approvalEmail2="peggy.newman@uq.edu.au";
	    	String emailListCoordEmail = "peggy.newman@uq.edu.au";
	    	
	    	String approvalMessage = "Hi ACRS, \n\nPlease find below details of an application for membership for your approval. \n\nKind Regards, \nThe ACRS Website\n\n";
	    	String emailListMessage = "Hi, \n\nThe following membership applicant indicated a desire to subscribe to the ACRS Mailing List. \n\nKind Regards, \nThe ACRS Website\n\n";
	    	String applicantDetail = "\n\tName:\t\t\t\t" + title + " " + firstName + " " + lastName 
	    						   + "\n\tAddress:\t\t\t" + streetAddress + ", " + city + " " + state + " " + postcode
	    						   + "\n\tEmail:\t\t\t" + email
	    						   + "\n\tPhone:\t\t\t" + phone
	    						   + "\n\tInstitution:\t\t" + institution
	    						   + "\n\tResearch Interest:\t" + researchInterest
	    						   + "\n\tNewsletter Preference:\t" + newsletterPref
	    						   + "\n\tMembership Type: \t\t" + membershipType
	    						   + "\n\tMembership Amount: \t" + membershipAmount + "0"
	    						   ;
	    	
	    	try {
	    		Emailer.sendEmail(approvalEmail1,"no-reply@acrs.org","New ACRS Membership", approvalMessage+applicantDetail);
	    		Emailer.sendEmail(approvalEmail2,"no-reply@acrs.org","New ACRS Membership", approvalMessage+applicantDetail);
	    		
	    		if (acrsEmailListFlag.equals("Y")) {
	    			Emailer.sendEmail(emailListCoordEmail,"no-reply@acrs.org","New ACRS Mail List Subscribe Request", emailListMessage+applicantDetail);
	    		}
	    		
	    	} catch (MessagingException e) {
	    		_log.fatal("Could not send email.");
	    	}
	    	
	    	actionResponse.setRenderParameter("newMemberId", String.valueOf(newMember.getId()));
	    	session.setAttribute("newMember", newMember, PortletSession.APPLICATION_SCOPE);
	    	session.setAttribute("paypalItemName", paypalItemName, PortletSession.APPLICATION_SCOPE);
    	}
    	
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
}
