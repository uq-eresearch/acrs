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

		// post back to PayPal system to validate
		// NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
		// using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
		// and configured for older versions.
		URL u = new URL("http://www.sandbox.paypal.com/cgi-bin/webscr");
		URLConnection uc = u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(str);
		pw.close();

		BufferedReader in = new BufferedReader(
		new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		in.close();

		// assign posted variables to local variables
		String memberID = request.getParameter("payer_id");
		String itemName = request.getParameter("item_name");
		String itemNumber = request.getParameter("item_number");
		String paymentStatus = request.getParameter("payment_status");
		String paymentAmount = request.getParameter("mc_gross");
		String paymentCurrency = request.getParameter("mc_currency");
		String txnId = request.getParameter("txn_id");
		String receiverEmail = request.getParameter("receiver_email");
		String payerEmail = request.getParameter("payer_email");

		// convert id to long
		
		
		//check notification validation
		if(res.equals("VERIFIED")) {
		// check that paymentStatus=Completed
		// check that txnId has not been previously processed
		// check that receiverEmail is your Primary PayPal email
		// check that paymentAmount/paymentCurrency are correct
		
			membersDao = ACRSApplication.getConfiguration().getUserDao();	
			Member thisMember = membersDao.getById(Long.parseLong(memberID));	

			thisMember.setPaypalRef(str);
			membersDao.save(thisMember);
			
			_log.info("paypal processed: " + str);
			// process payment
		}
		else if(res.equals("INVALID")) {
		// log for investigation
			_log.error("paypal problem: " + str);
		}
		else {
		// error
		}
	
		/*
		Enumeration en = req.getParameterNames();
		String str = "cmd=_notify-validate";
		while(en.hasMoreElements()){
			String paramName = (String)en.nextElement();
			String paramValue = req.getParameter(paramName);
			str = str + "&" + paramName + "=" + paramValue;
		}
		
		String idString = req.getParameter("id");
		String paymentStatus = req.getParameter("payment_status");
		String paymentAmount = req.getParameter("mc_gross");
		
		
		
		PrintWriter writer = res.getWriter();
		writer.print("Paypal Stuff saved");
		writer.print("id = " + idString);
		writer.print("paymentStatus = " + paymentStatus);
		writer.print("paymentAmount = " + paymentAmount);
		writer.println();
		writer.print("str = " + str);
		*/
	}
}