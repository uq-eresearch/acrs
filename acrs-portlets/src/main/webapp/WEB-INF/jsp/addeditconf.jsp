<%@ page import="org.acrs.portlets.ConferenceFormBean"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.acrs.data.model.ConferenceRegistration"%>
<%@ page import="java.util.List"%>
<%@ page import="org.acrs.data.access.ConferenceRegistrationDao"%>
<%@ page import="javax.portlet.*"%>
<%@ page import="org.apache.poi.hssf.usermodel.*" %>
<%@ page import="org.acrs.app.ACRSApplication"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<portlet:defineObjects />




<% 
	// find out what the command is
	String cmd = ParamUtil.getString(request, "cmd");
	ConferenceRegistration editMember = (ConferenceRegistration) renderRequest.getAttribute("editRegistration");
	ConferenceFormBean formBean = (ConferenceFormBean)renderRequest.getAttribute("formBean");
	
	
	List<String> errors = (List<String>) renderRequest.getAttribute("errors");
	if (errors != null && errors.size() > 0) {
		for (String error : errors) {
			%><div><span class="portlet-msg-error"><%=error%></span></div><%
        }
    }
	
	String formTitle = "";
	String emptyStr = "";
	boolean isEdit = false;
	
	boolean hasFormBean = formBean != null;
	
	if (cmd.equals("ADD")) {
		formTitle = "Conference Registration";
		
	}
	else if (cmd.equals("EDIT")) {
		formTitle = "Edit Conference Registration Details";
		isEdit = true;

	}
		 
 %>


<script type="text/javascript">


function updateTotalCost() {
	var registrationRate = jQuery("[name='registrationRate']:checked").val();
	var rate = 0;
	switch (registrationRate) {
		case 'StudentMember':
			rate = 330; break;
		case 'StudentNonMember':
			rate = 380; break;
		case 'FullMember':
			rate = 440; break;
		case 'FullNonMember':
			rate = 499; break;
		case 'DayOneOnly':
			rate = 240; break;
		case 'DayTwoOnly':
			rate = 240; break;
	}
	var studentMentoring = (jQuery("[name='studentMentoringDay']:checked").val() === 'true') ? 1 : 0;
	var coralFinder = (jQuery("[name='coralFinderWorkshop']:checked").val() === 'true') ? 1 : 0;
	var welcomeTickets = parseInt(jQuery("[name='additionalTicketsWelcome']").val()) || 0;
	var dinnerTickets = parseInt(jQuery("[name='additionalTicketsDinner']").val()) || 0;

	var regTotal = rate + (studentMentoring * -50) + (coralFinder * 10) + welcomeTickets * 20 + dinnerTickets * 30; 
	jQuery("#totalRegistrationCost").text("$" + regTotal);

}
jQuery(window).load( function() {
	jQuery("form select").change(updateTotalCost);
	jQuery("form input").keyup(updateTotalCost);
	jQuery("form input").change(updateTotalCost);
	updateTotalCost();
});

</script>
			
<div>


<form id="membershipForm" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">

<fieldset>
<legend>ACRS Conference Registration 2011</legend>

	<div>
		<label for="title">Title</label>
		<select name="title" id="title">
			<!-- >option value="Mr">Mr</option  -->
			<option value="Ms">Ms</option>
			<option value="Dr">Dr</option>
			<option value="Prof">Prof</option>
			<option selected="selected" 
			value="<%= isEdit ? StringEscapeUtils.escapeHtml(editMember.getTitle()) : "Mr" %>">
			<%= isEdit ? StringEscapeUtils.escapeHtml(editMember.getTitle()) : "Mr" %></option>
		</select>
	</div>

	<div>
		<label for="firstName">First Name:</label>
		<input type="text" name="firstName" id="firstName" class="required" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getFirstName()) : emptyStr %>">
	</div>
    <div>
		<label for="lastName">Last Name:</label>
		<input type="text" name="lastName" id="lastName" class="required" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getLastName()) : emptyStr %>">
	</div>

    <div>
		<label for="streetAddress">Street Address:</label>
		<input type="text" name="streetAddress" id="streetAddress" 
			value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getStreetAddress()) : emptyStr %>">
		<label for="streetAddress2">&nbsp;</label>
		<input type="text" name="streetAddress2" id="streetAddress2" 
			value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getStreetAddress2()) : emptyStr %>">
	</div>		

    <div>			
		<label for="city">Town or Suburb:</label>
		<input type="text" name="city" id="city" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getCity()) : emptyStr %>">
	</div>				
	
    <div>			
		<label for="state">State:</label>
		<input type="text" name="state" id="state" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getState()) : emptyStr %>">
	</div>				
	
    <div>	
		<label for="postcode">Postcode:</label>
		<input type="text" name="postcode" id="postcode" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getPostcode()) : emptyStr %>">
	</div>				
		
    <div>					
		<label for="country">Country:</label>
		<select name="country" id="country">
			<option selected="selected" 
			value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getCountry()) : "Australia" %>">
			<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getCountry()) : "Australia" %></option>
		<jsp:include page="/include/countrylist.jsp"/>
		</select>		
	</div>				

    <div>					
		<label for="email">Email:</label>
		<input type="text" name="email" id="email" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getEmail()) : emptyStr %>">
	</div>
	
	<div>					
		<label for="phone">Phone Number:</label>
		<input type="text" name="phone" id="phone" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getPhone()) : emptyStr %>">
	</div>
	
	<div>
		<label for="institution">Institution or Organisation and Branch or Department (if any):</label>
		<textarea rows="3" cols="70" name="institution" id="institution"><%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getInstitution()) : emptyStr %></textarea>
	</div>
	
	<br>
	<div>
	
		<label for="submittingAbstract">Have you submitted, or do you intend to submit an Abstract?</label>
		<input class="radioCheckbox" type="radio" name="submittingAbstract" value="true" <%= hasFormBean ? ("true".equals(formBean.getSubmittingAbstract()) ? " checked" : emptyStr) : emptyStr %>/>Yes <br>
	 	<input class="radioCheckbox" type="radio" name="submittingAbstract" value="false" <%= hasFormBean ? ("false".equals(formBean.getSubmittingAbstract()) ? " checked" : emptyStr) : emptyStr %>/>No <br>
	</div>
	<br>
	<div>							
		<label for="registrationRate">Select your registration rate:<br><br><br></label>
		<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentMember") ? " checked" : emptyStr) : emptyStr %>/> Student member: $330<br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentNonMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentNonMember") ? " checked" : emptyStr) : emptyStr %>/> Student non-member: $380<br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="FullMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullMember") ? " checked" : emptyStr) : emptyStr %>/> Full member: $440<br />		
		<input class="radioCheckbox" type="radio" name="registrationRate" value="FullNonMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullNonMember") ? " checked" : emptyStr) : emptyStr %>/> Full non-member: $499<br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="DayOneOnly" <%= hasFormBean ? (formBean.getRegistrationRate().equals("DayOneOnly") ? " checked" : emptyStr) : emptyStr %>/> Day rate &mdash; Day 1 only: $240<br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="DayTwoOnly" <%= hasFormBean ? (formBean.getRegistrationRate().equals("DayTwoOnly") ? " checked" : emptyStr) : emptyStr %>/> Day rate &mdash; Day 2 only: $240<br />

		<div>
		These costs will cover the welcome function, conference attendance, 2 lunches, and conference dinner.
		</div>
		
		<div>
		If you are not a member and wish to join, complete the <a href="http://www.australiancoralreefsociety.org/apply-individual" target="_blank">individual membership application form</a>.
		</div>
	</div>
	<br>
	<div>
		<label for="studentMentoringDay">Do you wish to attend the ARC Centre of Excellence for Coral Reef Studies Annual National Student Mentoring Day?</label>
		<input class="radioCheckbox" type="radio" name="studentMentoringDay" value="true" <%= hasFormBean ? ("true".equals(formBean.getStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr %>/>Yes <br>
	 	<input class="radioCheckbox" type="radio" name="studentMentoringDay" value="false" <%= hasFormBean ? ("false".equals(formBean.getStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr %>/>No <br>
	
		<div>
		If you are not a student, please tick No. If you are a student and want to attend, tick yes. $50 will be discounted from the total cost to assist you with accommodation fees.
		If you tick yes, but do not attend the whole student day, you will be later charged an additional $50.
		</div>
	
	</div>
	
	<br>
	<div>
		<label for="coralFinderWorkshop">Would you like to attend the Coral Finder Workshop?  <b>$??.00</b></label>
		<input class="radioCheckbox" type="radio" name="coralFinderWorkshop" value="true" <%= hasFormBean ? ("true".equals(formBean.getCoralFinderWorkshop())  ? " checked" : emptyStr) : emptyStr %>/>Yes <br>
	 	<input class="radioCheckbox" type="radio" name="coralFinderWorkshop" value="false" <%= hasFormBean ? ("false".equals(formBean.getCoralFinderWorkshop()) ? " checked" : emptyStr) : emptyStr %>/>No <br>

		<div>
		Mention extra cost here.
		</div>		
	</div>
	
	<br>
	<div>
		<label for="additionalTicketsWelcome">Would you like to purchase additional guest tickets to the welcome function? If so, please indicate how many additional tickets you would like.  <b>$??.00</b></label>
		
		<input type="text" name="additionalTicketsWelcome" id="additionalTicketsWelcome" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsWelcome()) : emptyStr %>">
	</div>
	
	<br>
	<div>
		<label for="additionalTicketsDinner">Would you like to purchase additional guest tickets to the conference dinner? If so, please indicate how many additional tickets you would like. <b>$??.00</b></label>
		
		<input type="text" name="additionalTicketsDinner" id="additionalTicketsDinner" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsDinner()) : emptyStr %>">
	</div>
	
	<% if (!isEdit) { %>
	<div class="captcha">
		<portlet:resourceURL var="captchaURL" id="captcha"/>
		<liferay-ui:captcha url="<%= captchaURL %>"/>
	</div>
	<% } %>
	

	<% if (isEdit) { %>
		<div>					
		<label for="registrationAmount">Registration Amount:</label>
		<input type="text" name="registrationAmount" id="registrationAmount" 
			value="<%= editMember.getRegistrationAmount() %>">
		</div>
		<div>					
		<label for="paypalStatus">PayPal Status:</label>
		<input type="text" name="paypalStatus" id="paypalStatus" 
			value="<%= editMember.getPaypalStatus() %>">
		</div>
	<%} %>

	
	<div>			
		
	<% if (isEdit) { %>

		  <label for="submit"><br></label>
		  <input name="editRegistrationId" type="hidden" value="<%= String.valueOf(editMember.getId()) %>"/>
		  <input id="removeFlag" name="removeFlag" type="hidden" value="N"/>
		  <input type="submit" name="submit" value="Save" 
			  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>
		  <input type="submit" name="submit" value="Remove" 
			  onClick="document.getElementById('removeFlag').value='Y'; self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>					  
	    
	<%  } else {%>
		
		<div>
			TOTAL COST OF REGISTRATION: <b><span id="totalRegistrationCost">$xxx</span></b> 
		</div>
		
		<div>
		  CANCELLATION POLICY<br>
			Registrations that are cancelled before 26 July 2011 will attract no penalty.<br>
			Registrations that are cancelled on or after 26 July 2011, but before 12 August 2011 will attract a 50% penalty.<br>
			Registrations that are cancelled on or after 12 August 2011 will receive no refund.<br>
					
		</div>
		  <label for="submit"><br></label>
		  <input type="submit" name="submit" value="Save Details and Proceed to Paypal >" 
			  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>

	<%} %>
	</div>


</fieldset>

</form>
</div>