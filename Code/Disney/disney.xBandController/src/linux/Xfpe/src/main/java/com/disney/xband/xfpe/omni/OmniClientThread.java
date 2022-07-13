package com.disney.xband.xfpe.omni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;

public class OmniClientThread extends Thread
{
	private static Logger logger = Logger.getLogger(OmniClientThread.class);
	
	private boolean run = true;
	private PrintWriter out = null;
	private Socket s;
	private OmniSimulator simulator;
	private OmniServer server;
	private int omniDeviceId = -1;

	public OmniClientThread(Socket s, OmniSimulator simulator, OmniServer server)
	{
		this.s = s;
		this.simulator = simulator;
		this.server = server;
	}
	
	public void stopThread()
	{
		run = false;
	}
	
	public void logoutReader(int omniDeviceId) {
		if (this.omniDeviceId != omniDeviceId)
			return;
		
		String xml = simulator.logoutReader(omniDeviceId);
		write(xml);
	}
	
	@Override
	public void run()
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(), true);

			while (run) {
				int a;
				StringBuilder instr = new StringBuilder();
				while ((a = in.read()) != 0) {
					if (a == -1)
						return;
					
					instr.append((char) a);
				}
				
				if (instr.length() > 0) {					
					AnswerCommand acmd = simulator.processMessage(instr.toString());
					
					if (acmd != null) {
						
						if (omniDeviceId == -1) {
							omniDeviceId = acmd.getDeviceID();
							logger.info("Reader with DeviceId " + omniDeviceId + " connected to the Omni simulator");
						}
						
						String axml = XmlUtil.convertToXml(acmd, AnswerCommand.class);
						write(axml);
					}
				}
			}
			
			out.close();
			s.close();
			
		} catch (IOException e) {
			logger.error("OmniServer: IO exception reading from socket", e);
		} catch (Exception e) {
			logger.error("OmniServer: IO exception reading from socket", e);
		}
		
		server.onOmniClientThreadExit(this);
	}
	
	public void write(String xml) {
		if (out == null)
			return;
		
		synchronized(out) {
			out.write(xml);
			out.append((char)0);
			out.flush();
		}
	}
	
	public int getOmniDeviceId()
	{
		return omniDeviceId;
	}
}
