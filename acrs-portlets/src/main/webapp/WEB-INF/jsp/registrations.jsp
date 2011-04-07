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
<legend>ACRS Conference Registrations 2011</legend>

<div style="text-align:right;" >
	<a href="<portlet:resourceURL id="spreadsheet"/>" >Export - Download </a>
</div>

<%-- <div style="width: 680px; height:500px; overflow:auto">--%>
<div style"overflow:auto">
<table id="memberListTable">
<tr>
<th>Name</th>
<th>Address</th>
<th>Email</th>
<th>Phone</th>
<th>Institution</th>
<th>Submitting Abstract</th>
<th>Registration<br>Rate</th>
<th>Student Mentoring Day</th>
<th>Coral Finder Workshop</th>
<th>Welcome Tickets</th>
<th>Dinner Tickets</th>
<th>Registration<br>Amount</th>
<th>Registration<br>Date</th>
<th>Last Updated<br>Date</th>
<th>Paypal<br>Status</th>
<th>Paypal<br>Reference</th>
<th>&nbsp;</th>
		
</tr>
<% for (ConferenceRegistration member : registrationsList) { 

	String paypalRefStr = "";
	String paypalDetails = "";
	String tableID ="paypalDetailsTable" + member.getId();
	
	//make the paypal info readable
	if (member.getPaypalRef() == null) {
		paypalRefStr = "Unavailable.";
	}
	else {
		String[] tokens = member.getPaypalRef().split("&");
		//paypalRefStr = "Paypal Details: <table border=0>";
		paypalRefStr = "<a href=\"#\" onclick=\"toggle_visibility('paypalDetailsTable" + member.getId() + "');\"> Paypal Details </a> ";
		paypalDetails = "<table id=\"paypalDetailsTable" + member.getId() + "\" style=\"border:0px; display:none\"> ";
		for(int i=0; i < tokens.length; i++) {
			paypalDetails = paypalDetails + "<tr><td>" + tokens[i] + "</td></tr>";
		}
		paypalDetails=paypalDetails + "</table>";
	}


	%>
	<tr>
	<td><%=StringEscapeUtils.escapeHtml(member.getTitle() + " " + member.getFirstName() + " " + member.getLastName())%></td>
	<td><%=StringEscapeUtils.escapeHtml(member.getStreetAddress() + ", " + member.getStreetAddress2() + ", " + member.getCity()) + "<br>" + StringEscapeUtils.escapeHtml(member.getState() + " " + member.getPostcode() + " " + member.getCountry())%></td>
	<td><%=StringEscapeUtils.escapeHtml(member.getEmail())%></td>
	<td><%=StringEscapeUtils.escapeHtml(member.getPhone())%></td>
	<td><%=StringEscapeUtils.escapeHtml(member.getInstitution())%></td>
	<td><%=member.getSubmittingAbstract() ? "Y" : "N" %></td>
	<td><%=StringEscapeUtils.escapeHtml(member.getRegistrationRate())%></td>
	<td><%=member.getStudentMentoringDay() ? "Y" : "N" %></td>
	<td><%=member.getCoralFinderWorkshop() ? "Y" : "N" %></td>
	<td><%=member.getAdditionalTicketsWelcome()%></td>
	<td><%=member.getAdditionalTicketsDinner()%></td>
	<td><%=member.getRegistrationAmount()%></td>
	<td><%=member.getRegistrationDate()%></td>
	<td><%=member.getUpdateDate()%></td>
	<td><%=member.getPaypalStatus()%></td>
	<td><%=paypalRefStr + paypalDetails%></td>
	<td>
	 <input type="button" value="EDIT" onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="EDIT" /><portlet:param name="registrationId" value="<%= String.valueOf(member.getId()) %>" /></portlet:renderURL>';"/>
	
	</td>
	</tr>
<%  } %>
</table>
</div>


</fieldset>







