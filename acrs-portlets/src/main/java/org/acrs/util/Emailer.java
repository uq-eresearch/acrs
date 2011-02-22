package org.acrs.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import org.acrs.app.ACRSApplication;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class Emailer {

    private static Log _log = LogFactoryUtil.getLog(Emailer.class);

    public static Configuration getEmailTemplateConfiguration() {
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(new ClassTemplateLoader(Emailer.class, "/templates/email"));
        cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        return cfg;
    }

    public static void sendEmail(String toEmailAddress, String fromEmailAddress, String subject, String text) throws MessagingException {
        Session session = Session.getDefaultInstance(ACRSApplication.getConfiguration().getSubmissionEmailConfig());
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmailAddress));
        message.setSubject(subject);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        message.setSentDate(new Date());
        message.setText(text);
        Transport.send(message);
        _log.info("Email Sent to " + toEmailAddress);
    }
}
