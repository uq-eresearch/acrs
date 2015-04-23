<%@page import="org.acrs.data.model.ConferenceRegistration"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.acrs.data.model.Member"%>
<%@ page import="java.util.List"%>
<%@ page import="org.acrs.data.access.MemberDao"%>
<%@ page import="javax.portlet.*"%>
<%@ page import="org.apache.poi.hssf.usermodel.*" %>
<%@ page import="org.acrs.app.ACRSApplication"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<portlet:defineObjects />




<% 
	// display the member list	

		List<ConferenceRegistration> registrationsList = (List<ConferenceRegistration>) renderRequest.getAttribute("allRegistrations");
		
		List<String> messages = (List<String>) renderRequest.getAttribute("messages");
		if (messages != null && messages.size() > 0) {
			for (String message : messages) {
				%><div><span class="portlet-msg-success"><%=message%></span></div><%
	        }
	    }
		
	%>

<fieldset>
<legend>ACRS Conference Registrations 2015</legend>

<div style="text-align:right;" >
	<a href="<portlet:resourceURL id="spreadsheet"/>" >Export - Download </a>
</div>

<div style="width: 680px; height:500px; overflow:auto">
<table id="memberListTable">
<tr>
<th>Name</th>
<th>Email</th>
<th>Institution</th>
<th>Phone Number</th>
<th>Submitting Abstract</th>
<th>Registration<br>Rate</th>
<th>Share With</th>
<th>Behaviour2015 Conference Delegate</th>
<th>Attend Student Mentoring Day</th>
<th>Student Mentoring Discount</th>
<th>SIMS Excursion</th>
<th>Coral Identification Workshop</th>
<th>Welcome Tickets</th>
<th>Dinner Tickets</th>
<th>Special Food Requirements</th>
<th>Registration<br>Amount</th>
<th>Registration<br>Date</th>
<th>Last Updated<br>Date</th>
<th>Paypal<br>Status</th>
<th>Paypal<br>Reference</th>
<th>&nbsp;</th>
		
</tr>
<% for (ConferenceRegistration registration : registrationsList) { 

	String paypalRefStr = "";
	String paypalDetails = "";
	String tableID ="paypalDetailsTable" + registration.getId();
	
	//make the paypal info readable
	if (registration.getPaypalRef() == null) {
		paypalRefStr = "Unavailable.";
	}
	else {
		String[] tokens = registration.getPaypalRef().split("&");
		//paypalRefStr = "Paypal Details: <table border=0>";
		paypalRefStr = "<a href=\"#\" onclick=\"toggle_visibility('paypalDetailsTable" + registration.getId() + "');\"> Paypal Details </a> ";
		paypalDetails = "<table id=\"paypalDetailsTable" + registration.getId() + "\" style=\"border:0px; display:none\"> ";
		for(int i=0; i < tokens.length; i++) {
			paypalDetails = paypalDetails + "<tr><td>" + tokens[i] + "</td></tr>";
		}
		paypalDetails=paypalDetails + "</table>";
	}


	%>
	<tr>
	<td><%=StringEscapeUtils.escapeHtml(registration.getTitle() + " " + registration.getFirstName() + " " + registration.getLastName())%></td>
	<td><%=StringEscapeUtils.escapeHtml(registration.getEmail())%></td>
	<td><%=StringEscapeUtils.escapeHtml(registration.getInstitution())%></td>
        <td><%=StringEscapeUtils.escapeHtml(registration.getPhone())%></td>
	<td><%=registration.getSubmittingAbstract() ? "Y" : "N" %></td>
	<td><%=StringEscapeUtils.escapeHtml(registration.getRegistrationRate())%></td>
        <td><%=StringEscapeUtils.escapeHtml(registration.getShareWith())%></td>
        <td><%=registration.getBehaviourDelegate() ? "Y" : "N" %></td>
	<td><%=registration.getAttendStudentMentoringDay() ? "Y" : "N" %></td>
	<td><%=registration.getStudentMentoringDiscount() ? "Y" : "N" %></td>
	<td><%=registration.getSimsExcursion() ? "Y" : "N" %></td>
	<td><%=registration.getCoralIdentificationWorkshop() ? "Y" : "N" %></td>
	<td><%=registration.getAdditionalTicketsWelcome()%></td>
	<td><%=registration.getAdditionalTicketsDinner()%></td>
	<td><%=registration.getSpecialFoodRequirements()%></td>
	<td><%=registration.getRegistrationAmount()%></td>
	<td><%=registration.getRegistrationDate()%></td>
	<td><%=registration.getUpdateDate()%></td>
	<td><%=registration.getPaypalStatus()%></td>
	<td><%=paypalRefStr + paypalDetails%></td>
	<td>
	 <input type="button" value="EDIT" onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="EDIT" /><portlet:param name="registrationId" value="<%= String.valueOf(registration.getId()) %>" /></portlet:renderURL>';"/>
	
	</td>
	</tr>
<%  } %>
</table>
</div>


</fieldset>







