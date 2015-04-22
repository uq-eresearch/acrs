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
	boolean isEarlybird = (Boolean)renderRequest.getAttribute("isEarlybird");
	int optionalLate = 0;
	if (!isEarlybird) {
		optionalLate = 30;
	}
	
	
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
	var isEarlybird = <%= isEarlybird %>;

	var amount = {
		'StudentMemberTwinShare': 648,
		'StudentMemberSingleRoom': 888,
		'StudentNonMemberTwinShare': 698,
		'StudentNonMemberSingleRoom': 938,
		'FullMemberTwinShare': 698,
		'FullMemberSingleRoom': 938,
		'FullNonMemberTwinShare': 748,
		'FullNonMemberSingleRoom': 988
	};

	rate = amount[registrationRate];
	
	if (!isEarlybird) {
		rate += 30
	}
	
	var studentMentoringDiscount = (jQuery("[name='studentMentoringDiscount']:checked").val() === 'true') ? 1 : 0;
	var coralIdentification = (jQuery("[name='coralIdentificationWorkshop']:checked").val() === 'true') ? 1 : 0;
	var welcomeTickets = parseInt(jQuery("[name='additionalTicketsWelcome']").val()) || 0;
	var dinnerTickets = parseInt(jQuery("[name='additionalTicketsDinner']").val()) || 0;

	var regTotal = rate + (studentMentoringDiscount * -70) + (coralIdentification * 330) + welcomeTickets * 25 + dinnerTickets * 99; 
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
<legend>ACRS Conference Registration 2015</legend>

	<div class="conference-inclusions">
		<h3>Cost of Registration</h3>
	
		<h4>Early Bird Registration (on or before 28th May 2015)</h4>
		<ul>
			<li>Student member twin share: $648</li>
			<li>Student member single occupancy: $888</li>
			<li>Student non-member twin share: $698</li>
			<li>Student non-member single occuancy: $938</li>

			<li>Full member twin share: $698</li>
			<li>Full member single occupancy: $938</li>
			<li>Full non-member twin share: $748</li>
			<li>Full non-member single occupancy: $988</li>
		</ul>
		 
		
		<h4>Late Registration (after 28th May 2015)</h4>
		<ul>
			<li>Student member twin share: $678</li>
			<li>Student member single occupancy: $918</li>
			<li>Student non-member twin share: $728</li>
			<li>Student non-member single occupancy: $968</li>

			<li>Full member twin share: $728</li>
			<li>Full member single occupancy: $968</li>
			<li>Full non-member twin share: $778</li>
			<li>Full non-member single occupancy: $1018</li>
		</ul>

		<!--<div>
			Current ACRS and ISRS members are eligible for the member registrations.
		</div>-->
		
		<h3>What's included in the registration price?</h3>
		<ul>
			<li>Transfers from Hamilton Island or Airlie Beach to Daydream Island</li>
			<li>3 nights accommodation in either twin share or single rooms</li>
			<li>All meals including dinner on 28th July and breakfast on 31st July (but not dinner on 29th July)</li>
			<li>Welcome function</li>
			<li>Conference dinner</li>
			<li>2 days of conference presentations</li>
			<li>Poster session</li>
		</ul>
	</div>

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
	
		<!-- TODO: make sure this saves in the DB -->
	<div>					
		<label for="phone">Phone Number: <span class="required">*</span></label>
		<input type="text" name="phone" id="phone" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getPhone()) : emptyStr %>">
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
			<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentMemberTwinShare" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentMemberTwinShare") ? " checked" : emptyStr) : emptyStr %>/> Student member twin: <b>$<%=648 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentMemberSingleRoom" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentMemberSingleRoom") ? " checked" : emptyStr) : emptyStr %>/> Student member single room: <b>$<%=888 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentNonMemberTwinShare" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentNonMemberTwinShare") ? " checked" : emptyStr) : emptyStr %>/> Student non-member twin share: <b>$<%= 698 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentNonMemberSingleRoom" <%= hasFormBean ? (formBean.getRegistrationRate().equals("StudentNonMemberSingleRoom") ? " checked" : emptyStr) : emptyStr %>/> Student non-member single room: <b>$<%= 938 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="FullMemberTwinShare" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullMemberTwinShare") ? " checked" : emptyStr) : emptyStr %>/> Full member twin share: <b>$<%= 698 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="FullMemberSingleRoom" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullMemberSingleRoom") ? " checked" : emptyStr) : emptyStr %>/> Full member single room: <b>$<%= 938 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="FullNonMemberTwinShare" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullNonMemberTwinShare") ? " checked" : emptyStr) : emptyStr %>/> Full non-member twin share: <b>$<%= 748 + optionalLate %></b><br />

			<input class="radioCheckbox" type="radio" name="registrationRate" value="FullNonMemberSingleRoom" <%= hasFormBean ? (formBean.getRegistrationRate().equals("FullNonMemberSingleRoom") ? " checked" : emptyStr) : emptyStr %>/> Full non-member single room: <b>$<%= 988 + optionalLate %></b><br />
		
		</div>
		<div>					
			<div><b>If you have selected "twin share"</b>, please write the name of the person you will share with in this box. If you do not have a person to share with, but would like to chose the "twin share" option, please write, "Unknown" in the box and we will pair you with another person:</div>
			<label for="sharewith"></label>
			<input type="text" name="sharewith" id="sharewith" 
			value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getShareWith()) : emptyStr %>">
		</div>

		<br/>

		<div>
		If you are not an ACRS member and wish to join, complete the <a href="http://www.australiancoralreefsociety.org/apply-individual" target="_blank">individual membership application form</a>.
		</div>
	</div>
	<br>
	<div>
		<div>Do you wish to attend the Student Mentoring Day?</div>
		<label for="attendStudentMentoringDay"></label>
		<div class="groupedinputs">
		  <input class="radioCheckbox" type="radio" name="attendStudentMentoringDay" value="true" <%=hasFormBean ? ("true".equals(formBean.getAttendStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr%>/>Yes
	 	  <input class="radioCheckbox" type="radio" name="attendStudentMentoringDay" value="false" <%=hasFormBean ? ("false".equals(formBean.getAttendStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr%>/>No
		</div>
		<div>
		If you are not a student, please tick No.
		</div>
		
		<div>If you do wish to attend the student day, are you eligible for the $70 discount?</div>
		<label for="studentMentoringDiscount"></label>
		<div class="groupedinputs">
		  <input class="radioCheckbox" type="radio" name="studentMentoringDiscount" value="true" <%=hasFormBean ? ("true".equals(formBean.getStudentMentoringDiscount()) ? " checked" : emptyStr) : emptyStr%>/>Yes
	 	  <input class="radioCheckbox" type="radio" name="studentMentoringDiscount" value="false" <%=hasFormBean ? ("false".equals(formBean.getStudentMentoringDiscount()) ? " checked" : emptyStr) : emptyStr%>/>No
		</div>
		<div>
		If you are not attending the day, please tick No.<br>
		To be eligible, you must attend the student day, and NOT be a Sydney-based student (note, there are only 40 places available).
		</div>
	
	</div>
	
	<br>
	<div>
		<label for="simsExcursion">Would you like to attend the excursion to SIMS?  <b>(no cost)</b></label>
		<div class="groupedinputs">
		<input class="radioCheckbox" type="radio" name="simsExcursion" value="true" <%= hasFormBean ? ("true".equals(formBean.getSimsExcursion())  ? " checked" : emptyStr) : emptyStr %>/>Yes
	 	<input class="radioCheckbox" type="radio" name="simsExcursion" value="false" <%= hasFormBean ? ("false".equals(formBean.getSimsExcursion()) ? " checked" : emptyStr) : emptyStr %>/>No
		</div>	
	</div>
	
	<br>
	<div>
		<label for="coralIdentificationWorkshop">Would you like to attend the Coral Identification Workshop?  <b>$330</b></label>
		<div class="groupedinputs">
		<input class="radioCheckbox" type="radio" name="coralIdentificationWorkshop" value="true" <%= hasFormBean ? ("true".equals(formBean.getCoralIdentificationWorkshop())  ? " checked" : emptyStr) : emptyStr %>/>Yes
	 	<input class="radioCheckbox" type="radio" name="coralIdentificationWorkshop" value="false" <%= hasFormBean ? ("false".equals(formBean.getCoralIdentificationWorkshop()) ? " checked" : emptyStr) : emptyStr %>/>No
		</div>	
	</div>
	
	<br>
	<div>
		<div>If you would like to purchase additional tickets to the Welcome Function at Sea Life Sydney Aquarium, please 
		enter the number of tickets in this box. Otherwise, please leave the box blank. Additional welcome function tickets are <b>$25.00&nbsp;ea</b></div>
		<label for="additionalTicketsWelcome"></label>
		
		<input type="text" name="additionalTicketsWelcome" id="additionalTicketsWelcome" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsWelcome()) : emptyStr %>">
	</div>
	
	<br>
	<div>
		<div>If you would like to purchase additional guest tickets to the Conference Dinner at the Star Room please
		 enter the number of tickets in this box. Otherwise, please leave the box blank. Additional tickets are <b>$99.00&nbsp;ea</b></div>
		<label for="additionalTicketsDinner"></label>
		
		<input type="text" name="additionalTicketsDinner" id="additionalTicketsDinner" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsDinner()) : emptyStr %>">
	</div>
	
	<div>
		<div>Please describe any special food requirements you have (e.g, vegetarian)</div>
		<label for="specialFoodRequirements"></label>
		
		<input type="text" name="specialFoodRequirements" id="specialFoodRequirements" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getSpecialFoodRequirements()) : emptyStr %>">
	</div>
	
	<% if (!isEdit) { %>
	<div class="captcha">
		<span class="required">*</span>
		<portlet:resourceURL var="captchaURL" id="captcha"/>
		<img class="captcha" src="<%= captchaURL %>"/>
		<label for="captchaText">4 digit text verification</label>
		<input type="text" name="captchaText">
<%--		<liferay-ui:captcha url="<%= captchaURL %>"/>  --%>
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
		
		<div style="color: red;">
		  CANCELLATION AND REFUND POLICY:<br>
Cancellations must be notified in writing to the Conference Organiser (<a href="mailto:ross.hill@unsw.edu.au">ross.hill@unsw.edu.au</a>).<br>
Cancellations before Tuesday 6 August 2013 will incur a $10.00 administration fee on their full refund.<br>
Cancellations on or after Tuesday 6 August 2013 will receive no refund.<br>
					
		</div>
		<div>
			Please note: When paying with PayPal, you will automatically receive a receipt to your email.
			If you require an additional receipt from ACRS, please let us know by emailing <a href="mailto:acrs@uq.edu.au">acrs@uq.edu.au</a>.
		</div>
		  <label for="submit"><br></label>
		  <input type="submit" name="submit" value="Save Details and Proceed to Paypal >" 
			  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>

	<%} %>
	</div>


</fieldset>

</form>
</div>
