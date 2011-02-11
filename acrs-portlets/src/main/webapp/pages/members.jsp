
<%@ page import="org.acrs.data.model.Member"%>
<%@ page import="java.util.List"%>

<%@ page import="org.acrs.data.access.MemberDao"%>
<%@ page import="javax.portlet.*"%>



<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<portlet:defineObjects />


<% 
	boolean isAdmin = (Boolean)renderRequest.getAttribute("isAdmin");
	if (isAdmin)
	{
%>

		<p>This user is an admin </p>

		<%
			List<Member> members = (List<Member>) renderRequest.getAttribute("allMembers");
		%>
		<div style="width: 680px; overflow:auto">
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
				
		</tr>
		<% for (Member member : members) { %>
		<tr>
		<td><%=member.getTitle() + " " + member.getFirstName() + " " + member.getLastName()%></td>
		<td><%=member.getStreetAddress() + ", " + member.getCity() + "<br>" + member.getState() + member.getPostcode() + " " + member.getCountry()%></td>
		<td><%=member.getEmail()%></td>
		<td><%=member.getPhone()%></td>
		<td><%=member.getInstitution()%></td>
		<td><%=member.getResearchInterest()%></td>
		<td><%=member.getNewsletterPref()%></td>
		<td><%=member.getMembershipType()%></td>
		</tr>
		<%} %>
		</table>
		</div>

		<!-- 
		
		String title = actionRequest.getParameter("title");
    	String firstName = actionRequest.getParameter("firstName");
    	String lastName = actionRequest.getParameter("lastName");
    	String streetAddress = actionRequest.getParameter("streetAddress");
    	String city = actionRequest.getParameter("city");
    	String state = actionRequest.getParameter("state");
    	String postcode = actionRequest.getParameter("postcode");
    	String country = actionRequest.getParameter("country");    	
    	String email = actionRequest.getParameter("email");
    	String phone = actionRequest.getParameter("phone");
    	String institution = actionRequest.getParameter("institution");
    	String researchInterest = actionRequest.getParameter("researchInterest");
    	String newsletterPref = actionRequest.getParameter("newsletterPref");
    	String membershipType = actionRequest.getParameter("membershipType");
		
		 -->










<%  }
	else
	{	
%>	
		<p>This user is NOT an admin   </p>


		<div>
		<form action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">
		
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
				<input type="text" name="firstName" id="firstName" >
			</div>
		    <div>
				<label for="lastName">Last Name:</label>
				<input type="text" name="lastName" id="lastName" >
			</div>
		
		    <div>
				<label for="streetAddress">Street Address:</label>
				<input type="text" name="streetAddress" id="streetAddress" >	<br>
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
				<input class="radioCheckbox" type="radio" name="membershipType" value="full" /> Full ($50.00)<br />
				<input class="radioCheckbox" type="radio" name="membershipType" value="student" /> Student ($30.00)<br />
				<input class="radioCheckbox" type="radio" name="membershipType" value="fiveYear" /> 5 Year Full ($200.00)<br />		
			</div>
			
			<div>			
				<label for="submit"><br></label>
				<input type="submit" name="submit" value="Submit and Proceed to Paypal >"/>
			</div>

		
		</fieldset>
		
		</form>
		</div>




<%
	}
%>

	
	







