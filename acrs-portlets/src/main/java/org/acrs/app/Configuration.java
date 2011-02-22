package org.acrs.app;

import org.acrs.data.access.MemberDao;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:03:57 PM
 */
public interface Configuration {

    /**
     * Return a member dao
     *
     * @return MemberDao
     */
    MemberDao getUserDao();
    Properties getSubmissionEmailConfig();
}
