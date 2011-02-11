package org.acrs.portlets;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.acrs.app.ACRSApplication;
import org.acrs.data.access.MemberDao;
import org.acrs.data.model.Member;

import javax.ccpp.SetAttribute;
import javax.portlet.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
    	include(viewJSP, renderRequest, renderResponse);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {
    	
    	//Process data here
    	List<String> errors = new ArrayList<String>();
    	
    	String title = actionRequest.getParameter("title");
    	String firstName = actionRequest.getParameter("firstName");
    	String lastName = actionRequest.getParameter("lastName");
    	String streetAddress = actionRequest.getParameter("streetAddress") + " " 
    						 + actionRequest.getParameter("streetAddress2");
    	String city = actionRequest.getParameter("city");
    	String state = actionRequest.getParameter("state");
    	String postcode = actionRequest.getParameter("postcode");
    	String country = actionRequest.getParameter("country");
    	String email = actionRequest.getParameter("email");
    	String phone = actionRequest.getParameter("phone");
    	String institution = actionRequest.getParameter("institution");
    	String researchInterest = actionRequest.getParameter("researchInterest");
    	String newsletterPref = actionRequest.getParameter("newsletterPref")+ ", " 
		 					  + actionRequest.getParameter("newsletterPref2");
    	String membershipType = actionRequest.getParameter("membershipType");
    	
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
    	  || (membershipType == null)|| membershipType.isEmpty()
    	 	) {
    		errors.add("All fields are required.");
    	}
    	
    	if (errors.size() > 0) {
            actionRequest.setAttribute("errors", errors);
        }
    	
    	Member newMember = new Member();
    	
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
    	
    	
    	membersDao.save(newMember);
    	
    	actionResponse.setRenderParameter("memberId", String.valueOf(newMember.getId()));
    	
    	
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
