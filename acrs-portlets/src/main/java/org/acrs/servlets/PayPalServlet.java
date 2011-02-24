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
		
		_log.info("Query String: " + request.getRequestURL());
		// read post from PayPal system and add 'cmd'
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while(en.hasMoreElements()){
		String paramName = (String)en.nextElement();
		String paramValue = request.getParameter(paramName);
		str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		}
		_log.info("Assembled String: " + str);
		// post back to PayPal system to validate
		// NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
		// using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
		// and configured for older versions.
		URL u = new URL("http://www.sandbox.paypal.com/cgi-bin/webscr");
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ACRSApplication.getConfiguration().getServerProxyName(), 8080));
		URLConnection uc = u.openConnection(proxy);

		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		
		_log.info("URLConnection open");

		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		_log.info("pw instantiated");
		pw.print(str);
		pw.close();
		
		_log.info("About to read response from paypal");
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		_log.info("Response: " + res);
		in.close();
		
		// assign posted variables to local variables
//		String memberID = request.getParameter("payer_id");
//		String itemName = request.getParameter("item_name");
//		String itemNumber = request.getParameter("item_number");
//		String paymentStatus = request.getParameter("payment_status");
//		String paymentAmount = request.getParameter("mc_gross");
//		String paymentCurrency = request.getParameter("mc_currency");
//		String txnId = request.getParameter("txn_id");
//		String receiverEmail = request.getParameter("receiver_email");
//		String payerEmail = request.getParameter("payer_email");

		// convert id to long
		
		
		//check notification validation
		if(res.equals("VERIFIED")) {
		// check that paymentStatus=Completed
		// check that txnId has not been previously processed
		// check that receiverEmail is your Primary PayPal email
		// check that paymentAmount/paymentCurrency are correct
		
/*			membersDao = ACRSApplication.getConfiguration().getUserDao();	
			Member thisMember = membersDao.getById(Long.parseLong(memberID));	

			thisMember.setPaypalRef(str);
			membersDao.save(thisMember);
*/			
			_log.info("paypal response = VERIFIED: " + str);
			// process payment
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