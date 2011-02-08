package org.acrs.portlets;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.acrs.app.ACRSApplication;
import org.acrs.data.access.MemberDao;

import javax.portlet.*;
import java.io.IOException;

/**
 * Author: alabri
 * Date: 08/02/2011
 * Time: 4:26:58 PM
 */
public class MembersPortlet extends GenericPortlet {
    private static Log _log = LogFactoryUtil.getLog(MembersPortlet.class);
    protected String viewJSP;
    protected MemberDao userDao;

    public void init() throws PortletException {
        viewJSP = getInitParameter("user-jsp");
        userDao = ACRSApplication.getConfiguration().getUserDao();
    }

    public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
        include(viewJSP, renderRequest, renderResponse);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {

    }


    protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

        PortletContext portletContext = getPortletContext();
        PortletRequestDispatcher portletRequestDispatcher = portletContext.getRequestDispatcher(path);
        if (portletRequestDispatcher == null) {
            _log.error(path + " is not a valid include");
        } else {
            try {
                portletRequestDispatcher.include(renderRequest, renderResponse);
            } catch (Exception e) {
                _log.error(e, e);
                portletRequestDispatcher = portletContext.getRequestDispatcher("/error.jsp");

                if (portletRequestDispatcher == null) {
                    _log.error("/error.jsp is not a valid include");
                } else {
                    portletRequestDispatcher.include(renderRequest, renderResponse);
                }
            }
        }
    }

    public void destroy() {
        if (_log.isInfoEnabled()) {
            _log.info("Destroying portlet");
        }
    }
}
