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
	ConferenceRegistration editRegistration = (ConferenceRegistration) renderRequest.getAttribute("editRegistration");
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

	var regTotal = rate + (studentMentoring * -50) + (coralFinder * 250) + welcomeTickets * 35 + dinnerTickets * 60; 
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


<form id="registrationForm" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">

<fieldset>
<legend>ACRS Conference Registration 2011</legend>

	<div>
		<label for="title">Title: <span class="required">*</span></label>
		<select name="title" id="title">
			<!-- >option value="Mr">Mr</option  -->
			<option value="Ms">Ms</option>
			<option value="Dr">Dr</option>
			<option value="Prof">Prof</option>
			<option selected="selected" 
			value="<%= isEdit ? StringEscapeUtils.escapeHtml(editRegistration.getTitle()) : "Mr" %>">
			<%= isEdit ? StringEscapeUtils.escapeHtml(editRegistration.getTitle()) : "Mr" %></option>
		</select>
	</div>

	<div>
		<label for="firstName">First Name: <span class="required">*</span></label>
		<input type="text" name="firstName" id="firstName" class="required" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getFirstName()) : emptyStr %>">
	</div>
    <div>
		<label for="lastName">Last Name: <span class="required">*</span></label>
		<input type="text" name="lastName" id="lastName" class="required" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getLastName()) : emptyStr %>">
	</div>

    <div>					
		<label for="email">Email: <span class="required">*</span></label>
		<input type="text" name="email" id="email" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getEmail()) : emptyStr %>">
	</div>
	
	<div>
		<label for="institution">Institution or Organisation and Branch or Department (if any):</label>
		<textarea rows="3" cols="70" name="institution" id="institution"><%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getInstitution()) : emptyStr %></textarea>
	</div>
	
	<br>
	<div>
	
		<label for="submittingAbstract">Have you submitted, or do you intend to submit an Abstract?</label>
		<div class="groupedinputs">
		<input class="radioCheckbox" type="radio" name="submittingAbstract" value="true" <%= hasFormBean ? ("true".equals(formBean.getSubmittingAbstract()) ? " checked" : emptyStr) : emptyStr %>/>Yes
	 	<input class="radioCheckbox" type="radio" name="submittingAbstract" value="false" <%= hasFormBean ? ("false".equals(formBean.getSubmittingAbstract()) ? " checked" : emptyStr) : emptyStr %>/>No
	 	</div>
	</div>
	<br>
	<div>							
		<label for="registrationRate">Select your registration rate: <span class="required">*</span><br><br><br></label>
		<div class="groupedinputs">
		<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentMember") ? " checked" : emptyStr) : emptyStr %>/> Student member: <b>$330</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentNonMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentNonMember") ? " checked" : emptyStr) : emptyStr %>/> Student non-member: <b>$380</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="FullMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullMember") ? " checked" : emptyStr) : emptyStr %>/> Full member: <b>$440</b><br />		
		<input class="radioCheckbox" type="radio" name="registrationRate" value="FullNonMember" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullNonMember") ? " checked" : emptyStr) : emptyStr %>/> Full non-member: <b>$499</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="DayOneOnly" <%= hasFormBean ? (formBean.getRegistrationRate().equals("DayOneOnly") ? " checked" : emptyStr) : emptyStr %>/> Day rate &mdash; Day 1 only: <b>$240</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="DayTwoOnly" <%= hasFormBean ? (formBean.getRegistrationRate().equals("DayTwoOnly") ? " checked" : emptyStr) : emptyStr %>/> Day rate &mdash; Day 2 only: <b>$240</b><br />
		</div>
		<div>
		These costs will cover the welcome function, conference attendance, 2 lunches, and conference dinner.
		</div>
		
		<div>
		If you are not a member and wish to join, complete the <a href="http://www.australiancoralreefsociety.org/apply-individual" target="_blank">individual membership application form</a>.
		</div>
	</div>
	<br>
	<div>
		<div>Do you wish to attend the ARC Centre of Excellence for Coral Reef Studies Annual National Student Mentoring Day?</div>
		<label for="studentMentoringDay"></label>
		<div class="groupedinputs">
		  <input class="radioCheckbox" type="radio" name="studentMentoringDay" value="true" <%= hasFormBean ? ("true".equals(formBean.getStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr %>/>Yes
	 	  <input class="radioCheckbox" type="radio" name="studentMentoringDay" value="false" <%= hasFormBean ? ("false".equals(formBean.getStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr %>/>No
		</div>
		<div>
		If you are not a student, please tick No. If you are a student and want to attend, tick yes. $50 will be discounted from the total cost to assist you with accommodation fees.
		If you tick yes, but do not attend the whole student day, you will be later charged an additional $50.
		</div>
	
	</div>
	
	<br>
	<div>
		<label for="coralIdentificationWorkshop">Would you like to attend the Coral Identification Workshop?  <b>$250</b></label>
		<div class="groupedinputs">
		<input class="radioCheckbox" type="radio" name="coralIdentificationWorkshop" value="true" <%= hasFormBean ? ("true".equals(formBean.getCoralIdentificationWorkshop())  ? " checked" : emptyStr) : emptyStr %>/>Yes
	 	<input class="radioCheckbox" type="radio" name="coralIdentificationWorkshop" value="false" <%= hasFormBean ? ("false".equals(formBean.getCoralIdentificationWorkshop()) ? " checked" : emptyStr) : emptyStr %>/>No
		</div>	
	</div>
	
	<br>
	<div>
		<div>If you would like to purchase additional guest tickets to the welcome function, please 
		enter the number of tickets in this box. Otherwise, please leave the box blank. Additional welcome function tickets are <b>$35.00&nbsp;ea</b></div>
		<label for="additionalTicketsWelcome"></label>
		
		<input type="text" name="additionalTicketsWelcome" id="additionalTicketsWelcome" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsWelcome()) : emptyStr %>">
	</div>
	
	<br>
	<div>
		<div>If you would like to purchase additional guest tickets to the conference dinner please
		 enter the number of tickets in this box. Otherwise, please leave the box blank. Additional tickets are <b>$60.00&nbsp;ea</b></div>
		<label for="additionalTicketsDinner"></label>
		
		<input type="text" name="additionalTicketsDinner" id="additionalTicketsDinner" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsDinner()) : emptyStr %>">
	</div>
	
	<% if (!isEdit) { %>
	<div class="captcha">
		<portlet:resourceURL var="captchaURL" id="captcha"/>
		<liferay-ui:captcha url="<%= captchaURL %>"/><span class="required">*</span>
	</div>
	<% } %>
	

	<% if (isEdit) { %>
		<div>					
		<label for="registrationAmount">Registration Amount:</label>
		<input type="text" name="registrationAmount" id="registrationAmount" 
			value="<%= editRegistration.getRegistrationAmount() %>">
		</div>
		<div>					
		<label for="paypalStatus">PayPal Status:</label>
		<input type="text" name="paypalStatus" id="paypalStatus" 
			value="<%= editRegistration.getPaypalStatus() %>">
		</div>
	<%} %>

	
	<div>			
		
	<% if (isEdit) { %>

		  <label for="submit"><br></label>
		  <input name="editRegistrationId" type="hidden" value="<%= String.valueOf(editRegistration.getId()) %>"/>
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
		<div>
			Please note: When paying with PayPal, you will automatically receive a receipt to your email.
			If you require an additional receipt from ACRS, please let us know by emailing <a href="mailto:acrs@cms.uq.edu.au">acrs@cms.uq.edu.au</a>.
		</div>
		  <label for="submit"><br></label>
		  <input type="submit" name="submit" value="Save Details and Proceed to Paypal >" 
			  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>

	<%} %>
	</div>


</fieldset>

</form>
</div>
