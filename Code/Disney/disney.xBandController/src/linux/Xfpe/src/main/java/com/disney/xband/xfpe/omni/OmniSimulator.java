package com.disney.xband.xfpe.omni;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.SocketException;
import java.util.Collection;

import javax.xml.bind.JAXBException;

import org.apache.catalina.tribes.util.Arrays;
import org.apache.log4j.Logger;

import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Connect;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Login;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Login.UserInfo;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Login.UserInfo.User;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand.Status;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand;
import com.disney.xband.xbrc.lib.net.NetInterface;
import com.disney.xband.xbrc.lib.omni.OmniConst;
import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xfpe.XfpeProperties;
import com.disney.xband.xfpe.controller.XfpeController;
import com.disney.xband.xfpe.model.XfpeReader;
import com.disney.xband.xfpe.simulate.PEGuestTest;
import com.disney.xband.xfpe.simulate.Simulator;
import com.disney.xband.xfpe.simulate.TestThread;

public class OmniSimulator {
	private static Logger logger = Logger.getLogger(OmniSimulator.class);
			
	public AnswerCommand processMessage(String xml) {
		try {
			RequestCommand cmd = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), RequestCommand.class);
			return processCommand(cmd);		
		} catch (JAXBException e) {
			logger.error("Failed to deserialize Omni xml request.", e);
			logger.debug(xml);
			return null;
		}		
	}
	
	public AnswerCommand processCommand(RequestCommand cmd) {
		if (cmd.getHeader().getRequestType().equals(OmniConst.REQ_TYPE_STATUS))
			return handleOmniStatus(cmd);
		if (cmd.getHeader().getRequestType().equals(OmniConst.REQ_TYPE_LOGOUT))
			return handleOmniLogout(cmd);
		if (cmd.getEntitlement() != null)
			return handleOmniEntitlement(cmd);
		else if (cmd.getLogin() != null)
			return handleOmniLogin(cmd);
		else if (cmd.getConnect() != null)
			return handleOmniConnect(cmd);
		else if (cmd.getWatchdog() != null)
			return handleOmniWatchdog(cmd);
		else
			logger.warn("Ignoring Omni answer of type: " + cmd.getHeader().getRequestType());
		
		return null;
	}
	
	AnswerCommand handleOmniEntitlement(RequestCommand cmd) {
		logger.info("Received Entitlement command");
		
		String bandId = cmd.getEntitlement().getMediaInfo().getMediaSearchMode().getXBandID();
		AnswerCommand acmd = makeEntitlementAnswer(cmd);
		
		if (XfpeProperties.getInstance().getXfpeSimConfig().isSimulateClosedReaderOnTor())
		{
			acmd.setEntitlement(null);
			acmd.getError().setErrorCode(BigInteger.valueOf(19));
			acmd.getError().setErrorDescription("ACCESS NOT PERMITTED");
			return acmd;
		}
		 		
		// Bio check is required by default.
		boolean bioRequired = true;
		
		PEGuestTest testRecord = Simulator.getInstance().getGuestTest(bandId);
		// No biometric scans for children.
		if (testRecord != null && testRecord.getChild())
			bioRequired = false;
		
		boolean validBand = testRecord == null || testRecord.getValidBand();
		
		// Invalid band so just reject it right away.
		if (!validBand) {
			acmd.getEntitlement().getEntitlementInfo().setBioRequired(false);
			acmd.getEntitlement().getEntitlementInfo().setDecremented(false);
			acmd.getEntitlement().getEntitlementInfo().setNewEnrollment(false);
			acmd.getError().setErrorCode(BigInteger.valueOf(101l));
			acmd.getError().setErrorDescription("Invalid band");
			return acmd;
		}
		
		// We have no bio template or no bio scan is required.
		if (cmd.getEntitlement().getMediaInfo().getBiometricInfo() == null || !bioRequired) { 
			acmd.getEntitlement().getEntitlementInfo().setBioRequired(bioRequired);
			acmd.getEntitlement().getEntitlementInfo().setDecremented(!bioRequired);
			acmd.getEntitlement().getEntitlementInfo().setNewEnrollment(true);
			return acmd;
		}
		
		// We have bio template from the reader and we need to compare it.
		if (cmd.getEntitlement().getMediaInfo().getBiometricInfo() != null) {
			// Guest is automatically entitled if we don't have a test case or the threshold check is zero
			boolean entitled = testRecord == null || XfpeProperties.getInstance().getXfpeSimConfig().getBioMatchThreshold() == 0;
			acmd.getEntitlement().getEntitlementInfo().setNewEnrollment(true);
			
			if (testRecord != null) {
				com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Entitlement.MediaInfo.BiometricInfo bi = 
						cmd.getEntitlement().getMediaInfo().getBiometricInfo();
				entitled = bi.getBiometricTemplate().equals(testRecord.getBioTemplate());
				logger.debug("Biometric comparison result for bandId " + bandId + " was " + entitled);
				acmd.getEntitlement().getEntitlementInfo().setNewEnrollment(false);
				acmd.getEntitlement().getEntitlementInfo().setBioRequired(true);
				if (!entitled) {
					if (cmd.getHeader().getRetryCounter() >= XfpeProperties.getInstance().getXfpeSimConfig().getMaxScanRetry()) {
						// Send some number bigger than 9 (anything less than 10 is valid according to Omni rules)
						acmd.getError().setErrorCode(BigInteger.valueOf(100l));
						acmd.getError().setErrorDescription("Max retry reached for biometric template match");
					}
				}
			}
			
			acmd.getEntitlement().getEntitlementInfo().setDecremented(entitled);
			return acmd;
		}
		
		return null;
	}

	AnswerCommand handleOmniLogin(RequestCommand cmd) {
		logger.info("Received Login command");
		XfpeReader r = XfpeController.getInstance().getReaderByDeviceId(cmd.getDeviceID());
		AnswerCommand acmd = makeLoginAnswer(cmd);
		if (r == null) {
			// Allow physical readers to login as well..
			//acmd.getError().setErrorCode(BigInteger.valueOf(101));
			//acmd.getError().setErrorDescription("INVALID DEVICE ID");
			return acmd;
		}
		
		r.setLoggedInUserId(acmd.getLogin().getUserInfo().getUser().getNumericId());
		return acmd;
	}
	
	AnswerCommand handleOmniConnect(RequestCommand cmd) {
		logger.info("Received Connect command");
		
		XfpeReader r = XfpeController.getInstance().getReaderByDeviceId(cmd.getDeviceID());
		AnswerCommand acmd = makeConnectAnswer(cmd);
		if (r == null) {
			// Allow physical readers to connect as well..
			// acmd.getError().setErrorCode(BigInteger.valueOf(101));
			// acmd.getError().setErrorDescription("INVALID DEVICE ID");
			return acmd;
		}
		r.setConnected(true);
		return acmd;
	}
	
	AnswerCommand handleOmniWatchdog(RequestCommand cmd) {
		logger.info("Received Watchdog command");
		return null;
	}
	
	AnswerCommand handleOmniStatus(RequestCommand cmd) {
		logger.info("Received Status command");
		XfpeReader r = XfpeController.getInstance().getReaderByDeviceId(cmd.getDeviceID());
		AnswerCommand acmd = makeStatusAnswer(cmd,r);
		if (r == null) {
			// Fake status of a physical reader as connected...
			// acmd.getError().setErrorCode(BigInteger.valueOf(101));
			// acmd.getError().setErrorDescription("INVALID DEVICE ID");
			return acmd;
		}
		return acmd;
	}
	
	AnswerCommand handleOmniLogout(RequestCommand cmd) {
		logger.info("Received Logout command");
		XfpeReader r = XfpeController.getInstance().getReaderByDeviceId(cmd.getDeviceID());
		AnswerCommand acmd = makeLogoutAnswer(cmd);
		
		if (r == null) {
			acmd.getError().setErrorCode(BigInteger.valueOf(101));
			acmd.getError().setErrorDescription("INVALID DEVICE ID");
			return acmd;
		}
		
		if (r.getLoggedInUserId() == null) {
			acmd.getError().setErrorCode(BigInteger.valueOf(102));
			acmd.getError().setErrorDescription("NO USER IS LOGGED IN");
			return acmd;
		}
		
		r.setLoggedInUserId(null);
		acmd.getError().setErrorDescription("USER: SOME USER LOGGED OUT");
		acmd.setLogout("");
		
		return acmd;
	}
	
	public static AnswerCommand makeAnswerCommand(RequestCommand rcmd) {
		AnswerCommand cmd = new AnswerCommand();
		cmd.setDeviceID(rcmd.getDeviceID());
		cmd.setError(new AnswerCommand.Error());
		cmd.getError().setErrorCode(BigInteger.ZERO);
		cmd.setHeader(new AnswerCommand.Header());
		cmd.getHeader().setReferenceNumber(rcmd.getHeader().getReferenceNumber());
		cmd.getHeader().setTransactionId(rcmd.getHeader().getTransactionId());
		return cmd;
	}
	
	public static AnswerCommand makeEntitlementAnswer(RequestCommand rcmd) {
		AnswerCommand cmd = makeAnswerCommand(rcmd);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_ENTITLEMENT);
		cmd.getHeader().setRequestSubType(OmniConst.REQ_SUBTYPE_UPDATE);
		AnswerCommand.Entitlement ent = new AnswerCommand.Entitlement();
		cmd.setEntitlement(ent);
		AnswerCommand.Entitlement.MediaInfo mi = new AnswerCommand.Entitlement.MediaInfo();
		ent.setMediaInfo(mi);
		AnswerCommand.Entitlement.EntitlementInfo ei = new AnswerCommand.Entitlement.EntitlementInfo();
		ent.setEntitlementInfo(ei);
		mi.setXBandID(cmd.getEntitlement().getMediaInfo().getXBandID());
		return cmd;
	}
	
	public static AnswerCommand makeConnectAnswer(RequestCommand rcmd) {
		AnswerCommand cmd = makeAnswerCommand(rcmd);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_CONNECT);
		cmd.getHeader().setRequestSubType("");
		cmd.setConnect(new Connect());
		try {
			Collection<String> addr = NetInterface.getOwnIpAddress(null);
			cmd.getConnect().setDeviceIP(addr.isEmpty() ? "127.0.0.1" : addr.iterator().next());
		} catch (SocketException e) {
			cmd.getConnect().setDeviceIP("127.0.0.1");
		}		
		cmd.getConnect().setTorId((byte)-1);
		cmd.getConnect().setGroupID("1");
		cmd.getConnect().setAccessAreaId("1");
		cmd.getConnect().setWatchDogTimeout(BigInteger.valueOf(60000l));
		return cmd;
	}
	
	public static AnswerCommand makeLoginAnswer(RequestCommand rcmd) {
		AnswerCommand cmd = makeAnswerCommand(rcmd);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_LOGIN);
		cmd.getHeader().setRequestSubType("");
		cmd.setLogin(new Login());
		cmd.getLogin().setUserInfo(new UserInfo());
		cmd.getLogin().getUserInfo().setUser(new User());
		cmd.getLogin().getUserInfo().getUser().setAlphaId(rcmd.getLogin().getUserInfo().getUser().getAlphaId());
		cmd.getLogin().getUserInfo().getUser().setNumericId(BigInteger.valueOf(new Long(rcmd.getLogin().getUserInfo().getUser().getAlphaId().hashCode())));
		return cmd;
	}
	
	public static AnswerCommand makeStatusAnswer(RequestCommand rcmd, XfpeReader r) {
		AnswerCommand cmd = makeAnswerCommand(rcmd);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_STATUS);
		cmd.getHeader().setRequestSubType("");
		cmd.setStatus(new Status());
		if (r == null || r.getLoggedInUserId() == null)
			return cmd;
		
		cmd.getStatus().setUserInfo(new AnswerCommand.Status.UserInfo());
		cmd.getStatus().getUserInfo().setUser(new AnswerCommand.Status.UserInfo.User());
		cmd.getStatus().getUserInfo().getUser().setAlphaId("SOME USER");
		cmd.getStatus().getUserInfo().getUser().setNumericId(r.getLoggedInUserId());
		return cmd;
	}
	
	public static AnswerCommand makeLogoutAnswer(RequestCommand rcmd) {
		AnswerCommand cmd = makeAnswerCommand(rcmd);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_LOGOUT);
		cmd.getHeader().setRequestSubType("");
		return cmd;
	}
	
	public static AnswerCommand makeLogoutAnswer(int omniDeviceId) {
		AnswerCommand cmd = new AnswerCommand();
		cmd.setDeviceID(omniDeviceId);
		cmd.setError(new AnswerCommand.Error());
		cmd.getError().setErrorCode(BigInteger.ZERO);
		cmd.getError().setErrorDescription("USER: SOME USER LOGGED OUT");
		cmd.setHeader(new AnswerCommand.Header());
		cmd.getHeader().setReferenceNumber("");
		cmd.getHeader().setTransactionId("");
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_LOGOUT);
		cmd.getHeader().setRequestSubType("");
		return cmd;
	}
	
	public String logoutReader(int omniDeviceId) {
		try {
			AnswerCommand acmd = makeLogoutAnswer(omniDeviceId);
			if (acmd == null)
				return null;
			String axml = XmlUtil.convertToXml(acmd, AnswerCommand.class);
			return axml;			
		} catch (JAXBException e) {
			logger.error("Failed to serialize logout omni command", e);
			return null;
		}		
	}
}
