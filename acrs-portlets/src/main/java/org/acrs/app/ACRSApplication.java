package org.acrs.app;

import au.edu.uq.itee.maenad.restlet.errorhandling.InitializationException;
import org.restlet.Application;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:03:30 PM
 */
public class ACRSApplication extends Application {

    private static final Configuration configuration;

    static {
        try {
            configuration = new ApplicationContext();
        } catch (InitializationException ex) {
            throw new RuntimeException("Failed to initialize application", ex);
        }
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
