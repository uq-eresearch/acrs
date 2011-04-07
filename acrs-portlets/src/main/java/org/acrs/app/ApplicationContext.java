package org.acrs.app;

import au.edu.uq.itee.maenad.restlet.errorhandling.InitializationException;
import au.edu.uq.itee.maenad.util.BCrypt;

import org.acrs.data.access.ConferenceRegistrationDao;
import org.acrs.data.access.MemberDao;
import org.acrs.data.access.jpa.ConferenceRegistrationDaoImpl;
import org.acrs.data.access.jpa.JpaConnectorService;
import org.acrs.data.access.jpa.MemberDaoImpl;
import org.acrs.data.model.Member;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:03:46 PM
 */
public class ApplicationContext implements Configuration, ServletContextListener {
    private final EntityManagerFactory emf;
    private final JpaConnectorService connectorService;
    private final MemberDao memberDao;
    private final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private final Properties submissionEmailConfig;
    private final String serverProxy;
    private final String paypalIpnUrl;
    private final String baseUrl;
    private final String approvalEmail1;
    private final String approvalEmail2;
    private final String emailListCoordEmail;
	private ConferenceRegistrationDaoImpl conferenceRegDao;

    public ApplicationContext() throws InitializationException {
        Properties properties = new Properties();
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = ApplicationContext.class.getResourceAsStream("/acrs.properties");
            if (resourceAsStream == null) {
                throw new InitializationException("Configuration file not found, please ensure " +
                        "there is an 'acrs.properties' on the classpath");
            }
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            throw new InitializationException("Failed to load configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }
        // try to load additional developer-specific settings
        File devPropertiesFile = new File("local/acrs.properties");
        if (devPropertiesFile.isFile()) {
            InputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(devPropertiesFile);
                properties.load(fileInputStream);
            } catch (FileNotFoundException ex) {
                // should never happen since we checked before
            } catch (IOException ex) {
                throw new InitializationException("Failed to load development configuration properties", ex);
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException ex) {
                        // so what?
                    }
                }
            }
        }

        final String persistenceUnitName = getProperty(properties, "persistenceUnitName");
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                emf.close();
            }
        }));
        this.connectorService = new JpaConnectorService(emf);
        this.memberDao = new MemberDaoImpl(this.connectorService);
        this.conferenceRegDao = new ConferenceRegistrationDaoImpl(this.connectorService);
        //TODO remove this later. This is only an example
        if (memberDao.getAll().isEmpty()) {
            // ensure that there's always one user to begin with
            createDefaultUsers();
        }

        this.submissionEmailConfig = new Properties();
        String submissionEmailServer = getProperty(properties, "emailServer");
        this.serverProxy = getProperty(properties, "serverProxyName");
        this.paypalIpnUrl = getProperty(properties, "paypalIpnUrl");
        this.baseUrl = getProperty(properties, "baseUrl");

        this.approvalEmail1 = getProperty(properties, "approvalEmail1");
        this.approvalEmail2 = getProperty(properties, "approvalEmail2");
        this.emailListCoordEmail = getProperty(properties, "emailListCoordEmail");
        this.submissionEmailConfig.setProperty("mail.smtp.host", submissionEmailServer);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.INFO, "Initialized ACRS Portlets application.........OK");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            emf.close();
            connectorService.stop();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error while closing ACRS Portlets application", ex);
        }
        logger.log(Level.INFO, "Closed ACRS Portlets application.........OK");
    }

    @Override
    public MemberDao getUserDao() {
        return memberDao;
    }

    @Override
    public Properties getSubmissionEmailConfig() {
        return submissionEmailConfig;
    }

    @Override
    public String getServerProxyName() {
        return serverProxy;
    }

    public String getPaypalIpnUrl() {
        return paypalIpnUrl;
    }

    @Override
    public JpaConnectorService getJpaConnectorService() {
        return connectorService;
    }

    private static String getProperty(Properties properties, String propertyName) throws InitializationException {
        return getProperty(properties, propertyName, false);
    }

    private static String getProperty(Properties properties, String propertyName, boolean allowEmpty) throws InitializationException {
        String result = properties.getProperty(propertyName);
        if (result == null || (!allowEmpty && result.isEmpty())) {
            throw new InitializationException(String.format("Failed to load required property '%s', " +
                    "please check configuration", propertyName));
        }
        return result;
    }

    private void createDefaultUsers() {
        Member admin = new Member("John", "Smith", "john@acrs.org", BCrypt.hashpw("acrs", BCrypt.gensalt()));
        memberDao.save(admin);
        Logger.getLogger(getClass().getName()).log(Level.INFO,
                "Created new a user with email address 'john@acrs.org' and password 'acrs'.");
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApprovalEmail1() {
        return approvalEmail1;
    }

    public String getApprovalEmail2() {
        return approvalEmail2;
    }

    public String getEmailListCoordEmail() {
        return emailListCoordEmail;
    }

	@Override
	public ConferenceRegistrationDao getConferenceRegistrationDao() {
		return conferenceRegDao;
	}
    
    
}