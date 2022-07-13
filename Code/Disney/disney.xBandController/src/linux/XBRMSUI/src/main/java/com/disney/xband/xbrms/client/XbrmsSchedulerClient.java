package com.disney.xband.xbrms.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.scheduler.ui.SchedulerClient;
import com.disney.xband.common.scheduler.ui.SchedulerRequest;
import com.disney.xband.common.scheduler.ui.SchedulerResponse;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.SslUtils;
import com.disney.xband.xbrms.common.XbrmsUtils;

public class XbrmsSchedulerClient extends SchedulerClient {

	private static Logger logger = Logger.getLogger(XbrmsSchedulerClient.class);

	@Override
	public <T> SchedulerResponse send(SchedulerRequest req, T context)
			throws Exception
	{
		IRestCall caller = null;
		
		if (context == null)
			caller = XbrmsUtils.getRestCaller();	// currently selected xBRMS, might be global
		else
			caller = XbrmsUtils.getRestCaller(context.toString());
		
		SchedulerResponse response = caller.onSchedulerMessage(req);
		
		return response;
	}
	
	
}
