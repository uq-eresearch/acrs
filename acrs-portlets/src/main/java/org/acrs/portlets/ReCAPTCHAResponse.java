package org.acrs.portlets;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.portlet.PortletRequest;

import org.acrs.app.ACRSApplication;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReCAPTCHAResponse {

  private static Log _log = LogFactoryUtil.getLog(ReCAPTCHAResponse.class);

  private Boolean success;

  private ReCAPTCHAResponse(boolean success) {
    this.success = success;
  }

  public static boolean isSuccess(ReCAPTCHAResponse response) {
    return (response!=null) && (response.success!=null) && (response.success.booleanValue());
  }

  public static ReCAPTCHAResponse ok() {
    return new ReCAPTCHAResponse(true);
  }

  public static ReCAPTCHAResponse failed() {
    return new ReCAPTCHAResponse(false);
  }

  private static ReCAPTCHAResponse check(final String recaptchaResponse) {
    try {
      _log.info("recaptcha user response token: " + recaptchaResponse);
      if(StringUtils.isBlank(recaptchaResponse)) {
        return ReCAPTCHAResponse.failed();
      }
      final URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
      final HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
      con.setDoOutput(true);
      con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
      PrintWriter pw = new PrintWriter(con.getOutputStream());
      pw.print(String.format("secret=%s&response=%s",
          URLEncoder.encode("6LfoEBwTAAAAAKIpF_0tCxyxHMvuegxQXAo9KYYr", "UTF-8"),
          URLEncoder.encode(recaptchaResponse, "UTF-8")));
      pw.close();
      _log.info("Waiting for response from google recaptcha service");
      final String recaptchaJson = IOUtils.toString(con.getInputStream());
      _log.info("recaptcha google response: " + recaptchaJson);
      return new Gson().fromJson(recaptchaJson, ReCAPTCHAResponse.class);
    } catch(Exception e) {
      // just log that captcha processing failed but let the user through anyway.
      _log.info("captcha processing failed", e);
      return ReCAPTCHAResponse.ok();
    }
  }

  public static void check(final PortletRequest request) throws RegistrationProcessingException {
    if(ACRSApplication.getConfiguration().isCheckCaptcha()) {
      if(!ReCAPTCHAResponse.isSuccess(ReCAPTCHAResponse.check(
          ParamUtil.getString(request, "g-recaptcha-response")))) {
        throw new RegistrationProcessingException("Invalid captcha, try again.");
      }
    }
  }

}
