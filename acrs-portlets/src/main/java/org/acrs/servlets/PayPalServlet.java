package org.acrs.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PayPalServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
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
		
	}
}