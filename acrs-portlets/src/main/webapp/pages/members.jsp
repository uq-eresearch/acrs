<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="org.acrs.data.model.Member"%>
<%@ page import="java.util.List"%>
<%@ page import="org.acrs.data.access.MemberDao"%>
<%@ page import="javax.portlet.*"%>



<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<portlet:defineObjects />




<% 
	// find out what the command is
	String cmd = (String) renderRequest.getAttribute("cmd");

	// if user is an admin, display the member list	
	boolean isAdmin = (Boolean)renderRequest.getAttribute("isAdmin");
	if (isAdmin)
	{
%>

		<%
			List<Member> members = (List<Member>) renderRequest.getAttribute("allMembers");
		%>
		<fieldset>
		<legend>Australian Coral Reef Society Memberships</legend>
		
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
		<th>Membership<br>Type</th>
		<th>Membership<br>Renewal Flag</th>
		<th>Membership<br>Amount</th>
		<th>Registration<br>Date</th>
		<th>Paypal<br>Status</th>
		<th>Paypal<br>Reference</th>
				
		</tr>
		<% for (Member member : members) { %>
		<tr>
		<td><%=member.getTitle() + " " + member.getFirstName() + " " + member.getLastName()%></td>
		<td><%=member.getStreetAddress() + ", " + member.getCity() + "<br>" + member.getState() + " " + member.getPostcode() + " " + member.getCountry()%></td>
		<td><%=member.getEmail()%></td>
		<td><%=member.getPhone()%></td>
		<td><%=member.getInstitution()%></td>
		<td><%=member.getResearchInterest()%></td>
		<td><%=member.getNewsletterPref()%></td>
		<td><%=member.getMembershipType()%></td>
		<td><%=member.getRenewalFlag()%></td>
		<td><%=member.getMembershipAmount()+"0"%></td>
		<td><%=member.getRegistrationDate()%></td>
		<td><%=member.getPaypalStatus()%></td>
		<td><%=member.getPaypalRef()%></td>
		</tr>
		<%} %>
		</table>
		</div>
		</fieldset>
	

<%  }
	// user is not an admin, display form
	else
	{
		
		Member newMember = (Member) renderRequest.getPortletSession().getAttribute("newMember", PortletSession.APPLICATION_SCOPE);
		List<String> errors = (List<String>) renderRequest.getAttribute("errors");
		
		// if there's a member
		if (newMember == null) {
			cmd = "_FORM";

			if (errors != null && errors.size() > 0) {
				for (String error : errors) {
					%><div><span class="portlet-msg-error"><%=error%></span></div><%
		        }
		    }
		}
		else {
			cmd = "_SUBMIT";

		}
		
		%>	
		
		<p>Please do not use this form yet. It is still under development.</p>
		
		
			 <% if (cmd == "_FORM") { %>
	

			
			
			<div>
			
			
			
			<form id="membershipForm" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">
			
			<fieldset>
			<legend>Application for Membership</legend>
			
				<div>
					<label for="title">Title</label>
					<select name="title" id="title">
						<option value="Mr">Mr</option>
						<option value="Ms">Ms</option>
						<option value="Dr">Dr</option>
						<option value="Prof">Prof</option>
					</select>
				</div>
			
				<div>
					<label for="firstName">First Name:</label>
					<input type="text" name="firstName" id="firstName" class="required">
				</div>
			    <div>
					<label for="lastName">Last Name:</label>
					<input type="text" name="lastName" id="lastName" class="required">
				</div>
			
			    <div>
					<label for="streetAddress">Street Address:</label>
					<input type="text" name="streetAddress" id="streetAddress" >
					<label for="streetAddress2">&nbsp;</label>
					<input type="text" name="streetAddress2" id="streetAddress2" >
				</div>		
	
			    <div>			
					<label for="city">Town or Suburb:</label>
					<input type="text" name="city" id="city">
				</div>				
				
			    <div>			
					<label for="state">State:</label>
					<input type="text" name="state" id="state">
				</div>				
				
			    <div>	
					<label for="postcode">Postcode:</label>
					<input type="text" name="postcode" id="postcode">
				</div>				
					
			    <div>					
					<label for="country">Country:</label>
					<select name="country" id="country">
					<option selected="selected" value="Australia">Australia</option>
					<jsp:include page="/include/countrylist.jsp"/>
					</select>		
				</div>				
			
			    <div>					
					<label for="email">Email:</label>
					<input type="text" name="email" id="email">
				</div>
				
				<div>					
					<label for="phone">Phone Number:</label>
					<input type="text" name="phone" id="phone">
				</div>
				
				<div>
					<label for="institution">Institution or Organisation and Branch or Department (if any):</label>
					<textarea rows="3" cols="70" name="institution" id="institution"></textarea>
				</div>
				
				<div>				
					<label for="researchInterest">Research or Professional or other interests in coral reefs:</label>
					<textarea rows="3" cols="70" name="researchInterest" id="researchInterest"></textarea>
				</div>
				<br>
				<div>
				
					<label for="newsletterPref">I would like to receive the annual newsletter in:</label>
					<input class="inputCheckbox" type="checkbox" name="newsletterPref" value="PDF" />PDF <br>
					<input class="inputCheckbox" type="checkbox" name="newsletterPref2" value="Hard Copy" />Hard Copy
				 
				</div>
				<br>
				<div>							
					<label for="membershipType">Membership Type:<br><br><br></label>
					<input class="radioCheckbox" type="radio" name="membershipType" value="Full" /> Full ($50.00)<br />
					<input class="radioCheckbox" type="radio" name="membershipType" value="Student" /> Student ($30.00)<br />
					<input class="radioCheckbox" type="radio" name="membershipType" value="FiveYear" /> 5 Year Full ($200.00)<br />		
	
				<div>
				There is a $10.00 discount on Full and Student single year memberships for applications received between 1 January and 28 February each year. Memberships are valid until the end of the calendar year.</div>
				
				</div>
				
				<div>
					<label for="renewalFlag">&nbsp;</label>
					<input class="inputCheckbox" type="checkbox" name="renewalFlag" value="Y" />I am renewing my previous membership.<br>
				</div>
				
				<div>
					<label for="acrsEmailListFlag">&nbsp;</label>
					<input class="inputCheckbox" type="checkbox" name="acrsEmailListFlag" value="Y" />I would like to subscribe to the ACRS Email List.<br>
				</div>			
				
				<div>			
					<label for="submit"><br></label>
					<input type="submit" name="submit" value="Save Details and Proceed to Paypal >"/>
				</div>
	
			
			</fieldset>
			
			</form>
			</div>
			
			<% 
			} else if (cmd == "_SUBMIT") { 
			
				String paypalItemName = (String) renderRequest.getPortletSession().getAttribute("paypalItemName", PortletSession.APPLICATION_SCOPE);
			%>
				
				<fieldset>
				<legend>Application for Membership</legend>
			
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
					
				<p>Please click the "Pay Now" button to proceed to pay for your membership via Paypal.</p>
				<p><b><%=paypalItemName + ": $" + newMember.getMembershipAmount() + "0" %></b> </p>
				
				<form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
				<input type="hidden" name="cmd" value="_xclick">
				<input type="hidden" name="business" value="V7JL47XMCL6GA">
				<input type="hidden" name="lc" value="AU">
				<input type="hidden" name="item_name" value="<%=paypalItemName%>">
				<input type="hidden" name="amount" value="<%=newMember.getMembershipAmount()+ "0" %>">
				<input type="hidden" name="currency_code" value="AUD">
				<input type="hidden" name="button_subtype" value="services">
				<input type="hidden" name="no_note" value="0">
				<input type="hidden" name="cn" value="Add special instructions to the seller">
				<input type="hidden" name="no_shipping" value="2">
				<input type="hidden" name="return" value="http://acrs.metadata.net/web/guest/home">
				<input type="hidden" name="cancel_return" value="http://acrs.metadata.net/web/guest/home">
				<input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHosted">
				<input type="image" src="https://www.sandbox.paypal.com/en_AU/i/btn/btn_buynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
				<img alt="" border="0" src="https://www.sandbox.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">
				
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
				
<!-- 				
				<form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
				<input type="hidden" name="cmd" value="_xclick">
				<input type="hidden" name="business" value="seller_1297898682_biz@uq.edu.au">
				<input type="hidden" name="lc" value="AU">
				<input type="hidden" name="item_number" value="<%=newMember.getId()%>">
				<input type="hidden" name="item_name" value="<%=paypalItemName%>">
				<input type="hidden" name="amount" value="<%=newMember.getMembershipAmount()+ "0" %>">
				<input type="hidden" name="currency_code" value="AUD">
				<input type="hidden" name="button_subtype" value="services">
				<input type="hidden" name="no_note" value="0">
				<input type="hidden" name="cn" value="Add special instructions to the seller">
				<input type="hidden" name="no_shipping" value="2">
				<input type="hidden" name="rm" value="1">				
				<input type="hidden" name="return" value="http://acrs.metadata.net/web/guest/home">
				<input type="hidden" name="cancel_return" value="http://acrs.metadata.net/web/guest/home">
				<input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHosted">
				<input type="image" src="https://www.sandbox.paypal.com/en_AU/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
				<img alt="" border="0" src="https://www.sandbox.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">
				</form>
				

  				
				<form target='_paypal' action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
				<input type="hidden" name="cmd" value="_s-xclick">
				<input type="hidden" name="hosted_button_id" value="265YJXAX586B6">
				<input type="hidden" name="amount" value="<%=newMember.getMembershipAmount()+ "0" %>">
				<input type="hidden" name="item_name" value="<%=paypalItemName%>">
				<input type="hidden" name="item_number" value="">
				
				<INPUT TYPE="hidden" NAME="first_name" VALUE="<%=newMember.getFirstName()%>">
				<INPUT TYPE="hidden" NAME="last_name" VALUE="<%=newMember.getLastName()%>">
				<INPUT TYPE="hidden" NAME="address1" VALUE="<%=newMember.getStreetAddress()%>">
				<INPUT TYPE="hidden" NAME="city" VALUE="<%=newMember.getCity()%>">
				<INPUT TYPE="hidden" NAME="state" VALUE="<%=newMember.getState()%>">
				<INPUT TYPE="hidden" NAME="zip" VALUE="<%=newMember.getPostcode()%>">
				<INPUT TYPE="hidden" NAME="country" VALUE="<%=newMember.getCountry()%> ">
				<INPUT TYPE="hidden" NAME="email" VALUE="<%=newMember.getEmail()%>">
				<INPUT TYPE="hidden" NAME="night_phone_a" VALUE="<%=newMember.getPhone()%>">
				
				<input type="image" src="https://www.sandbox.paypal.com/en_AU/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
				<img alt="" border="0" src="https://www.sandbox.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">
				</form>
-->				
			
			<%
			} // end if cmd == _SUBMIT 
			

	} // end if user(isAdmin)
%>

	
	







