<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.acrs.data.model.Member"%>
<%@ page import="java.util.List"%>
<%@ page import="org.acrs.data.access.MemberDao"%>
<%@ page import="javax.portlet.*"%>
<%@ page import="org.apache.poi.hssf.usermodel.*" %>
<%@ page import="org.acrs.app.ACRSApplication"%>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />




<% 
	// find out what the command is
	String cmd = ParamUtil.getString(request, "cmd");
	long memberId = ParamUtil.getLong(request, "memberId");
	String baseUrl = ACRSApplication.getConfiguration().getBaseUrl();
	
	boolean isAdmin = (Boolean)renderRequest.getAttribute("isAdmin");

	// maybe a submit, check for a new member
	Member newMember = (Member) renderRequest.getPortletSession().getAttribute("newMember", PortletSession.APPLICATION_SCOPE);

	MemberDao membersDao = (MemberDao) renderRequest.getAttribute("membersDao");
	Member editMember = (Member) membersDao.getById(memberId);
	
	
	if (!(cmd.equals("EDIT"))) {
		
		if (isAdmin) { 
			cmd = "MEMBERLIST";
			
		} else {
			cmd = "ADD"; 
		}

		if (newMember == null) {}  
		else {
			cmd = "SUBMIT";
		}
			
	} 
	
	

	// if user is an admin, display the member list	
	if (cmd.equals("MEMBERLIST"))
	{
		List<Member> membersList = (List<Member>) renderRequest.getAttribute("allMembers");
		
		List<String> messages = (List<String>) renderRequest.getAttribute("messages");
		if (messages != null && messages.size() > 0) {
			for (String message : messages) {
				%><div><span class="portlet-msg-success"><%=message%></span></div><%
	        }
	    }
		
	%>

		<fieldset>
		<legend>Australian Coral Reef Society Memberships</legend>
		
		<div style="text-align:right;" >
			<a href="<portlet:resourceURL id="spreadsheet"/>" >Export - Download </a>
		</div>
		
		<div style="width: 680px; height:500px; overflow:auto">
		<table id="memberListTable">
		<tr>
		<th>Name</th>
		<th>Address</th>
		<th>Email</th>
		<th>Phone</th>
		<th>Institution</th>
		<th>Research<br>Interest</th>		
		<th>Newsletter<br>Preference</th>		
		<th>Email List<br>Subscription</th>		
		<th>Membership<br>Type</th>
		<th>Membership<br>Renewal Flag</th>
		<th>Membership<br>Amount</th>
		<th>Registration<br>Date</th>
		<th>Last Updated<br>Date</th>
		<th>Paypal<br>Status</th>
		<th>Paypal<br>Reference</th>
		<th>&nbsp;</th>
				
		</tr>
		<% for (Member member : membersList) { 
		
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
			<td><%=member.getTitle() + " " + member.getFirstName() + " " + member.getLastName()%></td>
			<td><%=member.getStreetAddress() + ", " + member.getCity() + "<br>" + member.getState() + " " + member.getPostcode() + " " + member.getCountry()%></td>
			<td><%=member.getEmail()%></td>
			<td><%=member.getPhone()%></td>
			<td><%=member.getInstitution()%></td>
			<td><%=member.getResearchInterest()%></td>
			<td><%=member.getNewsletterPref()%></td>
			<td><%=member.getAcrsEmailListFlag()%></td>
			<td><%=member.getMembershipType()%></td>
			<td><%=member.getRenewalFlag()%></td>
			<td><%=member.getMembershipAmount()+"0"%></td>
			<td><%=member.getRegistrationDate()%></td>
			<td><%=member.getUpdateDate()%></td>
			<td><%=member.getPaypalStatus()%></td>
			<td><%=paypalRefStr + paypalDetails%></td>
			<td>
			 <input type="button" value="EDIT" onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="EDIT" /><portlet:param name="memberId" value="<%= String.valueOf(member.getId()) %>" /></portlet:renderURL>';"/>
			
			</td>
			</tr>
		<%  } %>
		</table>
		</div>
	
		
		</fieldset>

<%  } // end MEMBERLIST
	
	else if ( (cmd.equals("EDIT")) || (cmd.equals("ADD")) )
	{
		
		List<String> errors = (List<String>) renderRequest.getAttribute("errors");
		if (errors != null && errors.size() > 0) {
			for (String error : errors) {
				%><div><span class="portlet-msg-error"><%=error%></span></div><%
	        }
	    }
		
		String formTitle = "";
		String emptyStr = "";
		boolean isEdit = false;
		
		if (cmd.equals("ADD")) {
			formTitle = "Application for Individual Membership";
			
		}
		else if (cmd.equals("EDIT")) {
			formTitle = "Edit Membership Details";
			isEdit = true;

		}
		 
 %>
			
			<div>
			
			
			<form id="membershipForm" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">
			
			<fieldset>
			<legend><%=formTitle%></legend>
			
				<div>
					<label for="title">Title: <span class="required">*</span></label>
					<select name="title" id="title">
						<!-- >option value="Mr">Mr</option  -->
						<option value="Ms">Ms</option>
						<option value="Dr">Dr</option>
						<option value="Prof">Prof</option>
						<option selected="selected" 
						value="<%= isEdit ? editMember.getTitle() : "Mr" %>"><%= isEdit ? editMember.getTitle() : "Mr" %></option>
					</select>
				</div>
			
				<div>
					<label for="firstName">First Name: <span class="required">*</span></label>
					<input type="text" name="firstName" id="firstName" class="required" 
					value="<%= isEdit ? editMember.getFirstName() : emptyStr %>">
				</div>
			    <div>
					<label for="lastName">Last Name: <span class="required">*</span></label>
					<input type="text" name="lastName" id="lastName" class="required" 
					value="<%= isEdit ? editMember.getLastName() : emptyStr %>">
				</div>
			
			    <div>
					<label for="streetAddress">Street Address: <span class="required">*</span></label>
					<input type="text" name="streetAddress" id="streetAddress" 
					value="<%= isEdit ? editMember.getStreetAddress() : emptyStr %>">
               </div>
               <div>
					<label for="streetAddress2">&nbsp;</label>
					<input type="text" name="streetAddress2" id="streetAddress2" value="" >
				</div>		

			    <div>			
					<label for="city">Town or Suburb: <span class="required">*</span></label>
					<input type="text" name="city" id="city" 
					value="<%= isEdit ? editMember.getCity() : emptyStr %>">
				</div>				
				
			    <div>			
					<label for="state">State: <span class="required">*</span></label>
					<input type="text" name="state" id="state" 
					value="<%= isEdit ? editMember.getState() : emptyStr %>">
				</div>				
				
			    <div>	
					<label for="postcode">Postcode: <span class="required">*</span></label>
					<input type="text" name="postcode" id="postcode" 
					value="<%= isEdit ? editMember.getPostcode() : emptyStr %>">
				</div>				
					
			    <div>					
					<label for="country">Country: <span class="required">*</span></label>
					<select name="country" id="country">
						<option selected="selected" 
						value="<%= isEdit ? editMember.getCountry() : "Australia" %>"><%= isEdit ? editMember.getCountry() : "Australia" %></option>
					<jsp:include page="/include/countrylist.jsp"/>
					</select>		
				</div>				
			
			    <div>					
					<label for="email">Email: <span class="required">*</span></label>
					<input type="text" name="email" id="email" 
					value="<%= isEdit ? editMember.getEmail() : emptyStr %>">
				</div>
				
				<div>					
					<label for="phone">Phone Number: <span class="required">*</span></label>
					<input type="text" name="phone" id="phone" 
					value="<%= isEdit ? editMember.getPhone() : emptyStr %>">
				</div>
				
				<div>
					<label for="institution">Institution or Organisation and Branch or Department (if any): <span class="required">*</span></label>
					<textarea rows="3" cols="70" name="institution" id="institution">
					<%= isEdit ? editMember.getInstitution() : emptyStr %></textarea>
				</div>
				
				<div>				
					<label for="researchInterest">Research or Professional or other interests in coral reefs: <span class="required">*</span></label>
					<textarea rows="3" cols="70" name="researchInterest" id="researchInterest">
					<%= isEdit ? editMember.getResearchInterest() : emptyStr %></textarea>
				</div>
				<br>
				<div>
				
					<label for="newsletterPref">I would like to receive the annual newsletter in:</label>
					<input class="inputCheckbox" type="checkbox" name="newsletterPref" value="PDF" <%= isEdit ? (editMember.getNewsletterPref().startsWith("PDF") ? " checked" : emptyStr) : emptyStr %>/>PDF <br>
					<input class="inputCheckbox" type="checkbox" name="newsletterPref2" value="Hard Copy" <%= isEdit ? (editMember.getNewsletterPref().endsWith("Hard Copy") ? " checked" : emptyStr) : emptyStr %> />Hard Copy
				 
				</div>
				<br>
				<div>							
					<label for="membershipType">Membership Type: <span class="required">*</span><br><br><br></label>
					<input class="radioCheckbox" type="radio" name="membershipType" value="Full" <%= isEdit ? (editMember.getMembershipType().equals("Full") ? " checked" : emptyStr) : emptyStr %>/> Full ($50.00)<br />
					<input class="radioCheckbox" type="radio" name="membershipType" value="Student" <%= isEdit ? (editMember.getMembershipType().equals("Student") ? " checked" : emptyStr) : emptyStr %>/> Student ($30.00)<br />
					<input class="radioCheckbox" type="radio" name="membershipType" value="FiveYear" <%= isEdit ? (editMember.getMembershipType().equals("FiveYear") ? " checked" : emptyStr) : emptyStr %>/> 5 Year Full ($200.00)<br />		
					<!--  
					<input class="radioCheckbox" type="radio" name="membershipType" value="Test" <%= isEdit ? (editMember.getMembershipType().equals("Test") ? " checked" : emptyStr) : emptyStr %>/> Test ($5.00)<br />		
					-->  
	
				<% if (isEdit) { %>
					<div>					
					<label for="membershipAmount">Membership Amount:</label>
					<input type="text" name="membershipAmount" id="membershipAmount" 
					value="<%= isEdit ? editMember.getMembershipAmount()+ "0" : emptyStr %>">
					</div>
					<div>					
					<label for="paypalStatus">PayPal Status:</label>
					<input type="text" name="paypalStatus" id="paypalStatus" 
					value="<%= isEdit ? editMember.getPaypalStatus() : emptyStr %>">
					</div>
				<%} else { %>
					<div>
					There is a $10.00 discount on Full and Student single year memberships for applications received between 1 November (of the previous year) and 27 February. Memberships are valid until the end of the calendar year.</div>
					</div>
				<%} %>
				
				<div>
					<label for="renewalFlag">&nbsp;</label>
					<input class="inputCheckbox" type="checkbox" name="renewalFlag" value="Y" 
					<%= isEdit ? (editMember.getRenewalFlag().equals("Y") ? " checked" : emptyStr) : emptyStr %>/>I am renewing my previous membership.<br>
				</div>
				
				<div>
					<label for="acrsEmailListFlag">&nbsp;</label>
					<input class="inputCheckbox" type="checkbox" name="acrsEmailListFlag" value="Y" 
					<%= isEdit ? (editMember.getAcrsEmailListFlag().equals("Y") ? " checked" : emptyStr) : emptyStr %>/>I would like to subscribe to the ACRS Email List.<br>
				</div>			
				
					
					<!-- <input type="submit" name="submit" value="Save Details and Proceed to Paypal >"/> -->
					
					<% if (isEdit) { %>

					  <div>
						  <label for="submit"><br></label>
						  <input name="editMemberId" type="hidden" value="<%= String.valueOf(editMember.getId()) %>"/>
						  <input id="removeFlag" name="removeFlag" type="hidden" value="N"/>
						  <input type="submit" name="submit" value="Save" 
						  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>
			  			  <input type="submit" name="submit" value="Remove" 
						  onClick="document.getElementById('removeFlag').value='Y'; self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>					  
				      </div>
				    <%  } else {%>
						<% if(ACRSApplication.getConfiguration().isCheckCaptcha()) { %>
            <script src='https://www.google.com/recaptcha/api.js'></script>
            <div class="g-recaptcha" data-sitekey="6LfoEBwTAAAAAK9fHlQVmvTOgQHEYqsnJphqG7Dp"></div>
            			<% } %>
					<div>
					  For all new ACRS members your membership will need to be approved by Council. If for some reason your application is deemed inappropriate, a full refund will be given.					
					</div>
					  <label for="submit"><br></label>
					  <input type="submit" name="submit" value="Save Details and Proceed to Paypal >" 
					  onClick="self.location = '<portlet:renderURL><portlet:param name="cmd" value="SUBMIT"/></portlet:renderURL>';"/>

					<%} %>
	
			
			</fieldset>
			
			</form>
			</div>
			
	<% 
	}  // END ADD/EDIT
			
	else if (cmd.equals("SUBMIT")) { 
			
		String paypalItemName = (String) renderRequest.getPortletSession().getAttribute("paypalItemName", PortletSession.APPLICATION_SCOPE);
	%>
				
		<fieldset>
		<legend>Application for Individual Membership</legend>
	
		<p>Applicant Details</p>
	
		<table id="applicantDetailsTable">
		<tr><td class="applLabel">Name</td><td class="applData"><%=newMember.getTitle() + " " + newMember.getFirstName() + " " + newMember.getLastName()%></td></tr>
		<tr><td class="applLabel">Email</td><td class="applData"><%=newMember.getEmail()%></td></tr>
		<tr><td class="applLabel">Phone</td><td class="applData"><%=newMember.getPhone()%></td></tr>
<!-- 
				<tr><td class="applLabel">Address</td><td class="applData"><%=newMember.getStreetAddress() + ", " + newMember.getCity() + "<br>" + newMember.getState() + newMember.getPostcode() + " " + newMember.getCountry()%></td></tr>
		<tr><td class="applLabel">Institution</td><td class="applData"><%=newMember.getInstitution()%></td></tr>
		<tr><td class="applLabel">Research Interest</td><td class="applData"><%=newMember.getResearchInterest()%></td></tr>
		<tr><td class="applLabel">Newsletter Preference</td><td class="applData"><%=newMember.getNewsletterPref()%></td></tr>
-->
		<tr><td class="applLabel">Membership Type</td><td class="applData"><%=newMember.getMembershipType()%></td></tr>				
		<tr><td class="applLabel">Membership Amount</td><td class="applData"><%=newMember.getMembershipAmount()+ "0"%></td></tr>				
		</table>
		
		<br>
				
		<p>Please click the "Pay Now" button to proceed to pay for your membership via Paypal.<br>
		<p><font color="red"><b>Note: </b></font>You don't need to login to Paypal to process a credit card payment. After you click the Pay Now button above, you will be directed to PayPal and can pay as a Guest or you can use a PayPal account.</p>

		<p><b><%=paypalItemName + ": $" + newMember.getMembershipAmount() + "0" %></b> </p>
				
		
<%--
		<form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_xclick">
		<input type="hidden" name="business" value="V7JL47XMCL6GA">
		<input type="hidden" name="lc" value="AU">
		<input type="hidden" name="item_number" value="<%=newMember.getId()%>">
		<input type="hidden" name="item_name" value="<%=paypalItemName%>">
		<input type="hidden" name="amount" value="<%=newMember.getMembershipAmount()+ "0" %>">
		<input type="hidden" name="currency_code" value="AUD">
		<input type="hidden" name="button_subtype" value="services">
		<input type="hidden" name="no_note" value="1">
		<input type="hidden" name="no_shipping" value="1">
		<input type="hidden" name="rm" value="1">
		<input type="hidden" name="return" value="http://acrs.metadata.net/web/guest/end-application">
		<input type="hidden" name="cancel_return" value="http://acrs.metadata.net/web/guest/home">
		<input type="hidden" name="bn" value="PP-BuyNowBF:btn_paynowCC_LG.gif:NonHosted">
		<input type="hidden" name="notify_url" value="http://acrs.metadata.net/acrs-portlet/paypal/">
		<input type="image" src="https://www.sandbox.paypal.com/en_AU/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
		<img alt="" border="0" src="https://www.sandbox.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">				
--%>
 		
		<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_xclick">
		<input type="hidden" name="business" value="DWEN9JSL23L5U">
		<input type="hidden" name="lc" value="AU">
		<input type="hidden" name="item_number" value="<%=newMember.getId()%>">
		<input type="hidden" name="item_name" value="<%=paypalItemName%>">
		<input type="hidden" name="amount" value="<%=newMember.getMembershipAmount()+ "0" %>">
		<input type="hidden" name="currency_code" value="AUD">
		<input type="hidden" name="button_subtype" value="services">
		<input type="hidden" name="no_note" value="0">
		<input type="hidden" name="cn" value="Add special instructions to the seller">
		<input type="hidden" name="no_shipping" value="2">
        <input type="hidden" name="return" value="<%=baseUrl%>web/guest/end-application"> 
 		<input type="hidden" name="cancel_return" value="<%=baseUrl%>web/guest/home"> 
 		<input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHosted"> 
 		<input type="hidden" name="notify_url" value="<%=baseUrl%>acrs-portlet/paypal/"> 
		<input type="image" src="https://www.paypal.com/en_AU/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
		<img alt="" border="0" src="https://www.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">
		
		<INPUT TYPE="hidden" NAME="payer_id" VALUE="<%=newMember.getId()%>">
		<INPUT TYPE="hidden" NAME="first_name" VALUE="<%=newMember.getFirstName()%>">
		<INPUT TYPE="hidden" NAME="last_name" VALUE="<%=newMember.getLastName()%>">
		<INPUT TYPE="hidden" NAME="address1" VALUE="<%=newMember.getStreetAddress()%>">
		<INPUT TYPE="hidden" NAME="city" VALUE="<%=newMember.getCity()%>">
		<INPUT TYPE="hidden" NAME="state" VALUE="<%=newMember.getState()%>">
		<INPUT TYPE="hidden" NAME="zip" VALUE="<%=newMember.getPostcode()%>">
		<INPUT TYPE="hidden" NAME="country" VALUE="<%=newMember.getCountry()%> ">
		<INPUT TYPE="hidden" NAME="email" VALUE="<%=newMember.getEmail()%>">
		<INPUT TYPE="hidden" NAME="night_phone_a" VALUE="<%=newMember.getPhone()%>">				
		
		</form>
		
		<br>
		
		
		</fieldset>
	
			
	<% 	}
	
	%>

	
	







