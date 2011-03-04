package org.acrs.util;

import org.acrs.app.ACRSApplication;

public class AppUtil {
    /**
     * This method is used to clear the database cache
     */
    public static void clearCache() {
        ACRSApplication.getConfiguration().getJpaConnectorService().getEntityManager().clear();
    }
}
