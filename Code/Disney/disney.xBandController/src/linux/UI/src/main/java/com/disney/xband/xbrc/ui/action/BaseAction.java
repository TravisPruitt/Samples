package com.disney.xband.xbrc.ui.action;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.xbrc.ui.AuditInit;
import com.disney.xband.xbrc.ui.ControllerInfo;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.xbrc.lib.model.XbrcModel;
import com.disney.xband.xbrc.lib.net.NetInterface;

import javax.servlet.http.HttpSession;
import java.sql.Connection;

public class BaseAction extends ActionSupport implements Preparable {
	private static Logger logger = Logger.getLogger(BaseAction.class);
	private static final String ACTION_TITLE_DEFAULT = "Facility";
	private final static String XBRMS_URL = "nge.xconnect.ac.homeUrl";
	private static long lastAuditConfigCheck;

	private ControllerInfo ci;
	private String actionTitle;
	private String vip;
	private String xbrcName;

	@Override
	public void prepare() throws Exception {
		constructActionTitle();
		ci = UIProperties.getInstance().GetCachedControllerInfo();
		initAudit();

		final AuditConfig auditConfig = Auditor.getInstance().getConfig();

		if (auditConfig.isEnabled()) {
			auditConfig.setvHost(ServletActionContext.getRequest()
					.getServerName());
			auditConfig.setHost(ServletActionContext.getRequest()
					.getLocalName());
		}
	}

	public String getActionTitle() {
		return actionTitle;
	}

	private void constructActionTitle() {
		// get venue name from the Config table
		ControllerInfo confInfo = new ControllerInfo();
		Connection conn = null;

		try {
			conn = UIConnectionPool.getInstance().getConnection();
			Config.getInstance().read(conn, confInfo);

			if (!confInfo.getVenue().isEmpty())
				actionTitle = confInfo.getVenue();

			this.vip = confInfo.getVipAddress();
			this.xbrcName = confInfo.getName();

		} catch (Exception e) {
			logger.error(
					"Not able to ger venue name for tab title. Using default "
							+ ACTION_TITLE_DEFAULT, e);
		} finally {
			if (conn != null)
				UIConnectionPool.getInstance().releaseConnection(conn);
		}

		if (actionTitle == null)
			actionTitle = ACTION_TITLE_DEFAULT;

		// get your host name
		String hostName = NetInterface.getHostname();

		if (!hostName.isEmpty())
			if (actionTitle.length() > 0)
				actionTitle += " on " + hostName;
			else
				actionTitle += hostName;
	}

	public String getVenue() {
		return ci.getVenue();
	}

	public String getModel() {
		return ci.getModel();
	}

	public String getName() {
		return ci.getName();
	}

	public String getProductTitle() {
		return UIProperties.getInstance().getUiConfig().getProductTitle();
	}

	public String getUiVersion() {
		return UIProperties.getInstance().getUiVersion(
				ServletActionContext.getServletContext());
	}

	public String getXbrmsUrl() {
		final String url = UIProperties.getInstance().getProperty(
				BaseAction.XBRMS_URL);

		if (url != null) {
			return url;
		}

		final HttpSession session = ServletActionContext.getRequest()
				.getSession(false);

		if (session != null) {
			return (String) session.getAttribute(BaseAction.XBRMS_URL);
		}

		return null;
	}

	public void setXbrmsUrl(String url) {
		if (url != null) {
			final HttpSession session = ServletActionContext.getRequest()
					.getSession(false);

			if (session != null) {
				session.setAttribute(BaseAction.XBRMS_URL, url);
			}
		}
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public boolean isParkEntry() {
		if (getModel() != null
				&& XbrcModel.PARKENTRY.getXbrcModelClass().equals(getModel())) {
			return true;
		}

		return false;
	}

	public boolean isAttractionModel() {
		if (getModel() != null
				&& XbrcModel.ATTRACTION.getXbrcModelClass().equals(getModel())) {
			return true;
		}

		return false;
	}

	public boolean isSpaceModel() {
		if (getModel() != null
				&& XbrcModel.SPACE.getXbrcModelClass().equals(getModel())) {
			return true;
		}

		return false;
	}

	private void initAudit() {
		if ((System.currentTimeMillis() - lastAuditConfigCheck) > 30000) {
			final String venue = ci != null ? ci.getVenue() : "";
			AuditInit
					.initializeAudit(UIProperties.getInstance(), ci.getVenue());
			lastAuditConfigCheck = System.currentTimeMillis();
		}
	}
	
	public String getXbrcName() {
		return this.xbrcName;
	}
}
