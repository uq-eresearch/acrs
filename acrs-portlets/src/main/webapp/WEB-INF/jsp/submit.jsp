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
	String payPalUrl = ACRSApplication.getConfiguration().getPaypalIpnUrl();
	String paypalBusinessCode = ACRSApplication.getConfiguration().getPaypalBusinessCode();

	// maybe a submit, check for a new member
	ConferenceRegistration newRegistration = (ConferenceRegistration) renderRequest.getPortletSession().getAttribute("newRegistration", PortletSession.APPLICATION_SCOPE);

	String paypalItemName = (String) renderRequest.getPortletSession().getAttribute("paypalItemName", PortletSession.APPLICATION_SCOPE);
	%>
				
		<fieldset>
		<legend>Payment for 2017 Conference Registration</legend>
	
		<p>Registration Details</p>
	
		<table id="applicantDetailsTable">
		<tr><td class="applLabel">Name</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getTitle() + " " + newRegistration.getFirstName() + " " + newRegistration.getLastName()) %></td></tr>
		<tr><td class="applLabel">Email</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getEmail()) %></td></tr>
		<tr><td class="applLabel">Registration Rate</td><td class="applData"><%= StringEscapeUtils.escapeHtml(newRegistration.getRegistrationRate()) %></td></tr>				
		<tr><td class="applLabel">Registration Amount</td><td class="applData">$<%= newRegistration.getRegistrationAmount() %></td></tr>				
		</table>
		
		<br>
				
		<p>Please click the "Pay Now" button to proceed to pay for your registration via Paypal.</p>
                <p>Paypal allows credit card transactions; On the Paypal page please click "don't have a paypal account". if you want to pay directly with a credit card.</p>
		<p><b><%=paypalItemName + ": $" + newRegistration.getRegistrationAmount() %></b> </p>
		
		<form action="<%= payPalUrl %>" method="post" accept-charset="UTF-8">
		<input type="hidden" name="charset" value="UTF-8">
		<input type="hidden" name="cmd" value="_xclick">
		<input type="hidden" name="business" value="<%= paypalBusinessCode %>">
		<input type="hidden" name="lc" value="AU">
		<input type="hidden" name="item_number" value="<%=newRegistration.getId()%>">
		<input type="hidden" name="item_name" value="<%=paypalItemName%>">
		<input type="hidden" name="amount" value="<%=newRegistration.getRegistrationAmount() %>">
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
		
		
		
		<INPUT TYPE="hidden" NAME="payer_id" VALUE="<%=newRegistration.getId()%>">
		<INPUT TYPE="hidden" NAME="first_name" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getFirstName())%>">
		<INPUT TYPE="hidden" NAME="last_name" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getLastName())%>">
		<INPUT TYPE="hidden" NAME="email" VALUE="<%=StringEscapeUtils.escapeHtml(newRegistration.getEmail())%>">			
		
		</form>
		</fieldset>


	
	







