package org.acrs.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acrs.app.ACRSApplication;
import org.acrs.app.ApplicationContext;
import org.acrs.data.access.MemberDao;
import org.acrs.data.model.Member;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class PayPalServlet extends HttpServlet {

	private static Log _log = LogFactoryUtil.getLog(PayPalServlet.class);
	private static final long serialVersionUID = 1L;
	protected MemberDao membersDao;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// read post from PayPal system and add 'cmd'
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while(en.hasMoreElements()){
		String paramName = (String)en.nextElement();
		String paramValue = request.getParameter(paramName);
		str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		}
		_log.info("Return to paypal str: " + str);
		// post back to PayPal system to validate
		// NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
		// using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
		// and configured for older versions.
		URL u = new URL(ACRSApplication.getConfiguration().getPaypalIpnUrl());
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ACRSApplication.getConfiguration().getServerProxyName(), 8080));
		URLConnection uc = u.openConnection(proxy);

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
					
					membersDao = ACRSApplication.getConfiguration().getUserDao();	
					Member thisMember = membersDao.getById(Long.parseLong(itemNumber));	

					thisMember.setPaypalRef(str);
					thisMember.setPaypalStatus(paymentStatus);
					membersDao.save(thisMember);
					
				}
				catch (Exception e) {
					_log.error("Problem updating this member record: Member ID = " + itemNumber);
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
}