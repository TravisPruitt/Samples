package com.disney.xband.xbrc.parkentrymodel;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Connect;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Login;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Login.UserInfo;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Login.UserInfo.User;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Logout;
import com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.WorkRules;
import com.disney.xband.xbrc.lib.omni.OmniConst;

public class OmniTicket {
	
	private static Logger logger = Logger.getLogger(OmniTicket.class);
	
	public static RequestCommand makeRequestCommand(Integer deviceId, String referenceNumber, Long transactionId) {
		RequestCommand cmd = new RequestCommand();
		cmd.setDeviceID(deviceId);
		cmd.setHeader(new RequestCommand.Header());
		cmd.getHeader().setReferenceNumber(referenceNumber);
		cmd.getHeader().setTransactionId(transactionId.toString());
		cmd.getHeader().setInterfaceVersionID("V1.0");
		cmd.setWorkRules(new WorkRules());
		cmd.getWorkRules().getTags().add("All");
		return cmd;
	}
	
	public static RequestCommand makeEntitlementRequest(Integer deviceId, String referenceNumber, Long transactionId, Integer bioDeviceType) {
		RequestCommand cmd = makeRequestCommand(deviceId, referenceNumber, transactionId);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_ENTITLEMENT);
		cmd.getHeader().setRequestSubType(OmniConst.REQ_SUBTYPE_UPDATE);
		RequestCommand.Entitlement ent = new RequestCommand.Entitlement();
		cmd.setEntitlement(ent);
		RequestCommand.Entitlement.MediaInfo mi = new RequestCommand.Entitlement.MediaInfo();
		ent.setMediaInfo(mi);
		mi.setMediaSearchMode(new RequestCommand.Entitlement.MediaInfo.MediaSearchMode());
		mi.setBioDeviceFilter(bioDeviceType == null ? (byte)3 : (byte)bioDeviceType.intValue());
			
		return cmd;
	}
	
	public static RequestCommand makeBioEntitlementRequest(Integer deviceId, String referenceNumber, Long transactionId, Integer bioDeviceType) {
		RequestCommand cmd = makeEntitlementRequest(deviceId, referenceNumber, transactionId, bioDeviceType);
		cmd.getEntitlement().getMediaInfo().setBiometricInfo(new com.disney.xband.xbrc.OmniTicketLib.request.RequestCommand.Entitlement.MediaInfo.BiometricInfo());
		cmd.getEntitlement().getMediaInfo().getBiometricInfo().setBioDeviceType(bioDeviceType == null ? (byte)3 : (byte)bioDeviceType.intValue());
		return cmd;
	}
	
	public static RequestCommand makeConnectRequest(Integer deviceId) {
		RequestCommand cmd = makeRequestCommand(deviceId, "Connect", 1l);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_CONNECT);
		cmd.getHeader().setRequestSubType("");
		cmd.setConnect(new Connect());
		cmd.getConnect().setDeviceType((byte)30);
		cmd.getConnect().setStatus((byte)1);
		return cmd;
	}
	
	public static RequestCommand makeWatchdogRequest(Integer deviceId) {
		RequestCommand cmd = makeRequestCommand(deviceId, "Watchdog", 1l);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_WATCHDOG);
		cmd.getHeader().setRequestSubType("");
		return cmd;
	}
	
	public static RequestCommand makeLogonRequest(Integer deviceId, String bandId, String username, String password) {
		RequestCommand cmd = makeRequestCommand(deviceId, bandId, 1l);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_LOGIN);
		cmd.getHeader().setRequestSubType("");
		cmd.setLogin(new Login());
		cmd.getLogin().setUserInfo(new UserInfo());
		cmd.getLogin().getUserInfo().setUser(new User());
		cmd.getLogin().getUserInfo().getUser().setAlphaId(username);
		cmd.getLogin().getUserInfo().setPassword(password);
		return cmd;
	}
	
	public static RequestCommand makeLogoutRequest(Integer deviceId, String bandId) {
		RequestCommand cmd = makeRequestCommand(deviceId, bandId, 1l);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_LOGOUT);
		cmd.getHeader().setRequestSubType("");
		cmd.setLogout(new Logout());
		return cmd;
	}
	
	public static RequestCommand makeStatusRequest(Integer deviceId, String locationName) {
		RequestCommand cmd = makeRequestCommand(deviceId, locationName, 1l);
		cmd.getHeader().setRequestType(OmniConst.REQ_TYPE_STATUS);
		cmd.getHeader().setRequestSubType("");
		cmd.setLogout(new Logout());
		return cmd;
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
}
