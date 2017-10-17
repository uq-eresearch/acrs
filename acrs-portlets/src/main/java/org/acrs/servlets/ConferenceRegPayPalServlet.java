package org.acrs.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acrs.app.ACRSApplication;
import org.acrs.data.access.ConferenceRegistrationDao;
import org.acrs.data.model.ConferenceRegistration;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ConferenceRegPayPalServlet extends HttpServlet {

	private static Log _log = LogFactoryUtil.getLog(ConferenceRegPayPalServlet.class);
	private static final long serialVersionUID = 1L;
	protected ConferenceRegistrationDao conferenceRegDao;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String str = "cmd=_notify-validate";
		
		/*
		 * Paypal defaults to windows-1252 character encoding, which in Java
		 * is knows as CP1252. If this isn't set the IPN post back fails if there
		 * are any higher CP characters.
		 * 
		 * It is possible to use UTF-8, but the PayPal IPN simulator only support
		 * windows-1252, which makes testing more difficult.
		 * 
		 * Unfortunately, PayPal doesn't actually set the content encoding when sending
		 * the request, so we need to just magically know it. Or alternatively store and
		 * processes the raw bytes of the result ourselves. No thanks.
		 * 
		 * Refer to: http://blog.tentaclesoftware.com/archive/2010/04/09/87.aspx
		 */
		String characterEncoding = ACRSApplication.getConfiguration().getPaypalCharset();
		request.setCharacterEncoding(characterEncoding);
		
//		logRequestDebugging(request);

		// read post from PayPal system and add 'cmd'
		Enumeration<String> en = request.getParameterNames();
		while(en.hasMoreElements()){
		String paramName = en.nextElement();
			String paramValue = request.getParameter(paramName);
			str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue, characterEncoding);
		}
		_log.info("Return to paypal str: " + str);
		// post back to PayPal system to validate
		// NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
		// using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
		// and configured for older versions.
		URL u = new URL(ACRSApplication.getConfiguration().getPaypalIpnUrl());
		_log.debug("Paypal IPN URL: " + u.toString());
		URLConnection uc = u.openConnection();

		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.print(str);
		pw.close();
		
		_log.info("Waiting for response from paypal");
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		_log.info("Response: " + res);
		in.close();
		
		// assign posted variables to local variables

		String itemNumber = request.getParameter("item_number");
		String paymentStatus = request.getParameter("payment_status");
	
		
		//check notification validation
		if(res.equals("VERIFIED")) {
			
			_log.info("paypal response = VERIFIED: " + str);
			
			if (paymentStatus.equals("Completed")) {
				
				// try to find this member
				try {
					
					conferenceRegDao = ACRSApplication.getConfiguration().getConferenceRegistrationDao();	
					ConferenceRegistration thisRegistration = conferenceRegDao.getById(Long.parseLong(itemNumber));	

					thisRegistration.setPaypalRef(str);
					thisRegistration.setPaypalStatus(paymentStatus);
					
					String strPaymentAmount = request.getParameter("mc_gross");
					int paymentAmount = Math.round(Float.parseFloat(strPaymentAmount));
					int regAmount = thisRegistration.getRegistrationAmount();
					
					if (paymentAmount != regAmount) {
						thisRegistration.setPaypalStatus("Incorrect Payment Amount");
					}

					conferenceRegDao.save(thisRegistration);
					
				}
				catch (Exception e) {
					_log.error("Problem updating this conference registration record: Conference ID = " + itemNumber);
				}
				
			}
			else {
				_log.error("Payment status not COMPLETED: " + str);
			}
		}
		else if(res.equals("INVALID")) {
		// log for investigation
			_log.error("paypal response = INVALID: " + str);
		}
		else {
			_log.error("no paypal response" + str);
		// error
		}
	
	}


	
	private void logRequestDebugging(HttpServletRequest request) {

		String headers = "";
		@SuppressWarnings("unchecked")
		Enumeration<String> en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String headerName = en.nextElement();
			headers += headerName + ": " + request.getHeader(headerName) + "\n";
		}
		_log.info("IPN Headers: " + headers);
		
		_log.info("IPN Content Type: " + request.getContentType());
		

		String parameters = "";
		@SuppressWarnings("unchecked")
		Enumeration<String> enP = request.getParameterNames();

		while(enP.hasMoreElements()){
			String paramName = enP.nextElement();
			String paramValue = request.getParameter(paramName);
			parameters += paramName + ": " + paramValue + "\n";
		}
		_log.info("IPN Parameters: " + parameters);
			
		
	}
}