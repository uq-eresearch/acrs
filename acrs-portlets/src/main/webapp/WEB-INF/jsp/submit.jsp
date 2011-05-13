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
<portlet:defineObjects />
<% 
	// find out what the command is
	String baseUrl = ACRSApplication.getConfiguration().getBaseUrl();

	// maybe a submit, check for a new member
	ConferenceRegistration newRegistration = (ConferenceRegistration) renderRequest.getPortletSession().getAttribute("newRegistration", PortletSession.APPLICATION_SCOPE);

	String paypalItemName = (String) renderRequest.getPortletSession().getAttribute("paypalItemName", PortletSession.APPLICATION_SCOPE);
	%>
				
		<fieldset>
		<legend>Application for Individual Membership</legend>
	
		<p>Applicant Details</p>
	
		<table id="applicantDetailsTable">
		<tr><td class="applLabel">Name</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getTitle() + " " + newRegistration.getFirstName() + " " + newRegistration.getLastName()) %></td></tr>
		<tr><td class="applLabel">Email</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getEmail()) %></td></tr>
		<tr><td class="applLabel">Phone</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getPhone()) %></td></tr>
		<tr><td class="applLabel">Registration Rate</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getRegistrationRate()) %></td></tr>				
		<tr><td class="applLabel">Registration Amount</td><td class="applData">$<%= newRegistration.getRegistrationAmount() %></td></tr>				
		</table>
		
		<br>
				
		<p>Please click the "Pay Now" button to proceed to pay for your membership via Paypal.</p>
		<p><b><%=paypalItemName + ": $" + newRegistration.getRegistrationAmount() %></b> </p>
		
		<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_xclick">
		<input type="hidden" name="business" value="DWEN9JSL23L5U">
		<input type="hidden" name="lc" value="AU">
		<input type="hidden" name="item_number" value="<%=newRegistration.getId()%>">
		<input type="hidden" name="item_name" value="<%=paypalItemName%>">
		<input type="hidden" name="amount" value="<%=newRegistration.getRegistrationAmount()+ "0" %>">
		<input type="hidden" name="currency_code" value="AUD">
		<input type="hidden" name="button_subtype" value="services">
		<input type="hidden" name="no_note" value="0">
		<input type="hidden" name="cn" value="Add special instructions to the seller">
		<input type="hidden" name="no_shipping" value="2">
        <input type="hidden" name="return" value="<%=baseUrl%>web/guest/end-registration"> 
 		<input type="hidden" name="cancel_return" value="<%=baseUrl%>web/guest/home"> 
 		<input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHosted"> 
 		<input type="hidden" name="notify_url" value="<%=baseUrl%>acrs-portlet/confregpaypal/"> 
		<input type="image" src="https://www.paypal.com/en_AU/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
		<img alt="" border="0" src="https://www.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">
		
<%--
	For Testing Only!
		<form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
		<input type="hidden" name="cmd" value="_xclick">
		<input type="hidden" name="business" value="TN5A53Q7CXRC4">
		<input type="hidden" name="lc" value="AU">
		<input type="hidden" name="item_number" value="<%=newRegistration.getId()%>">
		<input type="hidden" name="item_name" value="<%=StringEscapeUtils.escapeHtml(paypalItemName)%>">
		<input type="hidden" name="amount" value="<%=newRegistration.getRegistrationAmount()%>">
		<input type="hidden" name="currency_code" value="AUD">
		<input type="hidden" name="button_subtype" value="services">
		<input type="hidden" name="no_note" value="0">
		<input type="hidden" name="no_shipping" value="1">
		<input type="hidden" name="rm" value="1">
        <input type="hidden" name="return" value="<%=baseUrl%>web/guest/end-registration"> 
 		<input type="hidden" name="cancel_return" value="<%=baseUrl%>web/guest/home"> 
 		<input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHosted"> 
 		<input type="hidden" name="notify_url" value="<%=baseUrl%>acrs-portlet/confregpaypal/"> 
		<input type="image" src="https://www.paypal.com/en_AU/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
		<img alt="" border="0" src="https://www.paypal.com/en_AU/i/scr/pixel.gif" width="1" height="1">
 --%>
		
		
		<INPUT TYPE="hidden" NAME="payer_id" VALUE="<%=newRegistration.getId()%>">
		<INPUT TYPE="hidden" NAME="first_name" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getFirstName())%>">
		<INPUT TYPE="hidden" NAME="last_name" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getLastName())%>">
		<INPUT TYPE="hidden" NAME="address1" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getStreetAddress())%>">
		<INPUT TYPE="hidden" NAME="address2" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getStreetAddress2())%>">
		<INPUT TYPE="hidden" NAME="city" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getCity())%>">
		<INPUT TYPE="hidden" NAME="state" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getState())%>">
		<INPUT TYPE="hidden" NAME="zip" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getPostcode())%>">
		<INPUT TYPE="hidden" NAME="country" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getCountry())%> ">
		<INPUT TYPE="hidden" NAME="email" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getEmail())%>">
		<INPUT TYPE="hidden" NAME="night_phone_a" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getPhone())%>">				
		
		</form>
		</fieldset>


	
	







