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
	String selectedStr = "selected=\"selected\"";
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

function hotelDays() {
  var ci = moment(document.getElementById('checkinDate').value, 'DD/MM/YYYY');
  var co = moment(document.getElementById('checkoutDate').value, 'DD/MM/YYYY');
  var days = co.diff(ci, 'days');
  return ((days>=0)&&(days<=31))?days:0;
}

function roomPerNight() {
  var roomType = jQuery("[name='hotelRoomType']:checked").val();
  if(roomType === 'hotelRoomDouble' || roomType === 'hotelRoomTwin') {
    return 129;
  } else {
    return 0;
  }
}

function updateTotalCost() {
	var registrationRate = jQuery("[name='registrationRate']:checked").val();
	var rate = 0;
	switch (registrationRate) {
		case 'StudentMember':
			rate = 330; break;
		case 'StudentNonMember':
			rate = 380; break;
		case 'FullMember':
			rate = 380; break;
		case 'FullNonMember':
			rate = 430; break;
	}
	var breakfast = (jQuery("[name='breakfastIncluded']:checked").val() === 'true') ? 1 : 0;
	var studentMentoringDiscount = (jQuery("[name='studentMentoringDiscount']:checked").val() === 'true') ? 1 : 0;
	var welcomeTickets = parseInt(jQuery("[name='additionalTicketsWelcome']").val()) || 0;
	var dinnerTickets = parseInt(jQuery("[name='additionalTicketsDinner']").val()) || 0;
	var regTotal = rate + (studentMentoringDiscount * - 60) + welcomeTickets * 30 + dinnerTickets * 70 +
		hotelDays() * (breakfast*20 + roomPerNight()); 
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
<legend>ACRS Conference Registration 2017</legend>

	<div class="conference-inclusions">
		<h3>Cost of Registration</h3>

		<ul>
			<li>Student member: $330</li>
			<li>Student non-member: $380</li>
			<li>Full member: $380</li>
			<li>Full non-member: $430</li>
		</ul>

		<h3>What's included in the registration price?</h3>
		<ul>
			<li>The Welcome Function on 16 July at Reef HQ, Townsville at 6 pm</li>
			<li>Two days of inspiring presentations, workshops and discussions on 17 and 18 July at Rydges Hotel, Townsville, on Palmer St</li>
			<li>A poster session on 17 July at 6 pm at Rydges Hotel</li>
			<li>A beautiful live performance piece on 17 July at 7:30 - 8:30 pm at Rydges Hotel</li>
			<li>Conference Awards dinner on 18 July at 7 pm at Rydges Hotel</li>
		</ul>
	</div>

	<div>
		<label for="title">Title: <span class="required">*</span></label>
		<select name="title" id="title">
			<option value="Prof" <%=hasFormBean?("Prof".equals(formBean.getTitle())?selectedStr:emptyStr):emptyStr%>>Prof</option>
			<option value="Dr" <%=hasFormBean?("Dr".equals(formBean.getTitle())?selectedStr:emptyStr):emptyStr%>>Dr</option>
			<option value="Ms" <%=hasFormBean?("Ms".equals(formBean.getTitle())?selectedStr:emptyStr):emptyStr%>>Ms</option>
			<option value="Mr" <%=hasFormBean?("Mr".equals(formBean.getTitle())?selectedStr:emptyStr):emptyStr%>>Mr</option>
<%--
			<option selected="selected" 
			value="<%= isEdit ? StringEscapeUtils.escapeHtml(editRegistration.getTitle()) : "Mr" %>">
			<%= isEdit ? StringEscapeUtils.escapeHtml(editRegistration.getTitle()) : "Mr" %></option>
--%>
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
		<input class="radioCheckbox" type="radio" name="submittingAbstract" value="true" <%= hasFormBean ?
			("true".equals(formBean.getSubmittingAbstract()) ? " checked" : emptyStr) : emptyStr %>/>Yes
		<input class="radioCheckbox" type="radio" name="submittingAbstract" value="false" <%= hasFormBean ?
			("false".equals(formBean.getSubmittingAbstract()) ? " checked" : emptyStr) : emptyStr %>/>No
		</div>
	</div>
	<br>

	<div>
		<label for="registrationRate">Select your registration rate: <span class="required">*</span><br><br><br></label>
		<div class="groupedinputs">
		<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentMember" <%= hasFormBean ?
			(formBean.getRegistrationRate().equals("StudentMember") ? " checked" : emptyStr) : emptyStr %>/> Student member: <b>$330</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="StudentNonMember" <%= hasFormBean ?
			(formBean.getRegistrationRate().equals("StudentNonMember") ? " checked" : emptyStr) : emptyStr %>/> Student non-member: <b>$380</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="FullMember" <%= hasFormBean ?
			(formBean.getRegistrationRate().equals("FullMember") ? " checked" : emptyStr) : emptyStr %>/> Full member: <b>$380</b><br />
		<input class="radioCheckbox" type="radio" name="registrationRate" value="FullNonMember" <%= hasFormBean ?
			(formBean.getRegistrationRate().equals("FullNonMember") ? " checked" : emptyStr) : emptyStr %>/> Full non-member: <b>$430</b><br />
		</div>
		<div>
			If you are not an ACRS member and wish to join, complete the <a href="http://www.australiancoralreefsociety.org/apply-individual"
				target="_blank">individual membership application form</a>.
		</div>
	</div>
	<br>

	<div>
		<div><b>Accommodation</b></div>
		<div>
			<label for="hotelRoomType">If you wish to stay at Rydges Hotel, select your room type:</label>
			<div class="groupedinputs">
				<input class="radioCheckbox" type="radio" name="hotelRoomType" value="hotelRoomDouble"<%=hasFormBean ?
					("hotelRoomDouble".equals(formBean.getHotelRoomType()) ? " checked" : emptyStr) : emptyStr%>/>
					Double room: <b>$129</b> per night per room<br>
				<input class="radioCheckbox" type="radio" name="hotelRoomType" value="hotelRoomTwin"<%=hasFormBean ? 
					("hotelRoomTwin".equals(formBean.getHotelRoomType()) ? " checked" : emptyStr) : emptyStr%>/>
					Twin room: <b>$129</b> per night per room<br>
				<input class="radioCheckbox" type="radio" name="hotelRoomType" value="hotelRoomNone"<%=hasFormBean ? 
					("hotelRoomNone".equals(formBean.getHotelRoomType()) ? " checked" : emptyStr) : emptyStr%>/>
					I will organise my own accommodation or another delegate has paid for the twin room I will share: <b>$0</b><br>
			</div>
		</div>

		<div><label for="breakfastIncluded">Would you like breakfast included ($20 per day)?</label>
			<div class="groupedinputs">
				<input class="radioCheckbox" type="radio" name="breakfastIncluded" value="true" <%=hasFormBean ?
					("true".equals(formBean.getBreakfastIncluded()) ? " checked" : emptyStr) : emptyStr%>/>Yes<br>
				<input class="radioCheckbox" type="radio" name="breakfastIncluded" value="false" <%=hasFormBean ?
					("false".equals(formBean.getBreakfastIncluded()) ? " checked" : emptyStr) : emptyStr%>/>No<br>
			</div>
		</div>
		<br>

		<div><label for="checkinDate">What date will you check in at Rydges Hotel?</label>
			<input type="text" id="checkinDate" name="checkinDate" value="<%=hasFormBean?formBean.getCheckinDate():emptyStr%>"/>
		</div>

		<div><label for="checkoutDate">What date will you check out of Rydges Hotel?</label>
			<input type="text" id="checkoutDate" name="checkoutDate" value="<%=hasFormBean?formBean.getCheckoutDate():emptyStr%>"/>
		</div>
<script>
	var checkInPicker = new Pikaday({
		field: document.getElementById('checkinDate'),
		format: 'DD/MM/YYYY',
		defaultDate: new Date('2017-7-16'),
		setDefaultDate: false,
		minDate: new Date('2017-7-1'),
		maxDate: new Date('2017-7-31')
	});
	var checkInPicker = new Pikaday({
		field: document.getElementById('checkoutDate'),
		format: 'DD/MM/YYYY',
		defaultDate: new Date('2017-7-19'),
		setDefaultDate: false,
		minDate: new Date('2017-7-1'),
		maxDate: new Date('2017-7-31')
	});
</script>

		<div>
			Do you need assistance from the ACRS organising committee to help find someone to share a twin room?
			<label for="assistShareTwinRoom"></label>
			<div class="groupedinputs">
				<input class="radioCheckbox" type="radio" name="assistShareTwinRoom" value="true" <%=hasFormBean ?
					("true".equals(formBean.getAssistShareTwinRoom()) ? " checked" : emptyStr) : emptyStr%>/>Yes
				<input class="radioCheckbox" type="radio" name="assistShareTwinRoom" value="false" <%=hasFormBean ?
					("false".equals(formBean.getAssistShareTwinRoom()) ? " checked" : emptyStr) : emptyStr%>/>No
			</div>
		</div>
	</div>

	<div>
		<div>Do you wish to attend the Student Mentoring Day?</div>
		<label for="attendStudentMentoringDay"></label>
		<div class="groupedinputs">
			<input class="radioCheckbox" type="radio" name="attendStudentMentoringDay" value="true" <%=hasFormBean ?
				("true".equals(formBean.getAttendStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr%>/>Yes
			<input class="radioCheckbox" type="radio" name="attendStudentMentoringDay" value="false" <%=hasFormBean ?
				("false".equals(formBean.getAttendStudentMentoringDay()) ? " checked" : emptyStr) : emptyStr%>/>No
		</div>
		<div>If you are not a student, please tick No.</div>
	</div>

	<div>
		<div>If you do wish to attend the student day, are you eligible for the $60 discount?</div>
		<label for="studentMentoringDiscount"></label>
		<div class="groupedinputs">
			<input class="radioCheckbox" type="radio" name="studentMentoringDiscount" value="true" <%=hasFormBean ?
				("true".equals(formBean.getStudentMentoringDiscount()) ? " checked" : emptyStr) : emptyStr%>/>Yes
			<input class="radioCheckbox" type="radio" name="studentMentoringDiscount" value="false" <%=hasFormBean ?
				("false".equals(formBean.getStudentMentoringDiscount()) ? " checked" : emptyStr) : emptyStr%>/>No
		</div>
		<div>
		If you are not attending the day, please tick No.<br>
		To be eligible, you must attend the student mentoring day,
		and NOT be a Townsville-based student (note, there are only 40 places available).
		</div>
	</div>
	
	<br>
	<div>
		<div>If you would like to purchase additional tickets to the Welcome Function at Reef HQ, please 
		enter the number of tickets in this box. Otherwise, please leave the box blank.
		Additional welcome function tickets are <b>$30.00&nbsp;ea</b></div>
		<label for="additionalTicketsWelcome"></label>
		<input type="number" min="0" max="100" name="additionalTicketsWelcome" id="additionalTicketsWelcome" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsWelcome()) : emptyStr %>">
	</div>
	
	<br>
	<div>
		<div>If you would like to purchase additional guest tickets to the Conference Dinner at 
		Rydges Hotel, please enter the number of tickets in this box. Otherwise, please leave the
		box blank. Additional tickets are <b>$70.00&nbsp;ea</b></div>
		<label for="additionalTicketsDinner"></label>
		<input type="number" min="0" max="100" name="additionalTicketsDinner" id="additionalTicketsDinner" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getAdditionalTicketsDinner()) : emptyStr %>">
	</div>
	
	<div>
		<div>Please describe any special food requirements you have (e.g, vegetarian)</div>
		<label for="specialFoodRequirements"></label>
		
		<input type="text" name="specialFoodRequirements" id="specialFoodRequirements" 
		value="<%= hasFormBean ? StringEscapeUtils.escapeHtml(formBean.getSpecialFoodRequirements()) : emptyStr %>">
	</div>
	
	<% if (!isEdit && ACRSApplication.getConfiguration().isCheckCaptcha()) { %>
		<script src='https://www.google.com/recaptcha/api.js'></script>
		<div class="g-recaptcha" data-sitekey="6LfoEBwTAAAAAK9fHlQVmvTOgQHEYqsnJphqG7Dp"></div>
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
Cancellations must be notified in writing to the Conference Organiser (<a href="mailto:selinaward@uq.edu.au">selinaward@uq.edu.au</a>).<br>
Cancellations before Friday 23 June 2017 will incur a $50.00 administration fee on their full refund.<br>
Cancellations on or after Friday 23 June 2017 will receive no refund.<br>
					
		</div>
		<div>
			Please note: When paying with PayPal, you will automatically receive a receipt to your email.
			If you require an additional receipt from ACRS, please let us know by emailing <a href="mailto:austcoralreefsoc@gmail.com">austcoralreefsoc@gmail.com</a>.
		</div>
		  <label for="submit"><br></label>
		  <input type="submit" name="submit" value="Save Details and Proceed to Paypal >" 
			  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>

	<%} %>
	</div>


</fieldset>

</form>
</div>
