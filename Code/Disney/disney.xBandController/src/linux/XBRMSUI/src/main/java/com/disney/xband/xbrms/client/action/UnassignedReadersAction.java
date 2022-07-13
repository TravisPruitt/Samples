package com.disney.xband.xbrms.client.action;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.xbrc.lib.config.HAStatusEnum;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.disney.xband.xbrms.common.IRestCall;
import com.disney.xband.xbrms.common.PkConstants;
import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.ReaderInfoListDto;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.common.model.XbrmsDto;
import com.opensymphony.xwork2.Preparable;

public class UnassignedReadersAction extends BaseAction implements Preparable {
    private static Logger logger = Logger.getLogger(UnassignedReadersAction.class);
    private static final int ID_GAP = 100000;

    private List<ReaderInfo> inventory;
    private String execResult;
    private int execStatus;
    private String readerMacs;
    private String macAddress;
    private Map<Long, String> xbrcs;
    private Map<Long, XbrcDto> dtos;
    private int xbrcId;
    private String xbrcVip;

    @Override
    public void prepare() throws Exception 
    {
        this.doPrepare();
	}

	@Override
	public String execute() throws Exception 
	{
        this.execResult = "";
        this.execStatus = 0;
        this.xbrcId = -1;

		return super.execute();
	}

    public String assign() throws Exception 
    {
    	String[] macs = null;

        if(this.readerMacs == null) {
            this.execStatus = 1;
            this.execResult = "You must select at least one reader.";

            if(logger.isInfoEnabled()) {
                logger.info("Failed to assigning readers (IDs: " + this.readerMacs + ")");
            }

            return SUCCESS;
        }
        else {
            macs = this.readerMacs.split(",");

            if((macs == null) || (macs.length == 0)) {
                this.execStatus = 1;
                this.execResult = "You must select at least one reader.";

                if(logger.isInfoEnabled()) {
                    logger.info("Failed to assigning readers (IDs: " + this.readerMacs + ")");
                }

                return SUCCESS;
            }
        }

        if((this.xbrcId <= 0) && (XbrmsUtils.isEmpty(this.xbrcVip))) {
            this.execStatus = 1;
            this.execResult = "You must either select xBRC from the list or specify its VIP";

            if(logger.isInfoEnabled()) {
                logger.info("Failed to assigning readers (IDs: " + this.readerMacs + ")");
            }

            return SUCCESS;
        }
        
        // verify that the server the user specified by hand is up and running
        if (!XbrmsUtils.isEmpty(this.xbrcVip))
        {
        	final int CONNECTION_TIMEOUT = 30000;
        	
        	// see if user provided the port number
        	int port = 8080;	// default port
        	String ip = null;
        	try
        	{
        		String[] address = this.xbrcVip.split(":");
        		ip = address[0];
        		if (address.length == 2)
        			port = Integer.parseInt(address[1]);
        	}
        	catch (Exception ignore){ /* default port will be used to verify that the xBRC is up and running */ }
        	
        	// attempt to contact the xBRC
        	try
        	{
        		Socket socket = new Socket();
        		InetAddress addr = InetAddress.getByName(ip);
        		SocketAddress sAddrs = new InetSocketAddress(addr, port);
        		socket.connect(sAddrs, CONNECTION_TIMEOUT);
        	}
        	catch(Exception e)
        	{
        		this.execStatus = 1;
                this.execResult = (macs.length == 1 ? "Reader " : "Readers: ") + this.readerMacs + " not assigned. Server " + this.xbrcVip + " is not responding.";

                if(logger.isInfoEnabled()) {
                    logger.info("User attempted to assign readers (IDs: " + this.readerMacs + ") to an unresponsive xBRC " + this.xbrcVip);
                }

                return SUCCESS;
        	}
        }

        final IRestCall caller = XbrmsUtils.getRestCaller(
        		XbrmsServerChooser.getInstance().getActiveXbrms(ServletActionContext.getRequest()).getUrl());
        final String xbrc;

        if (XbrmsUtils.isEmpty(this.xbrcVip)) 
        {
        	// xbrc selected from the drop down
            final XbrcDto item = this.dtos.get(new Long(this.xbrcId));
            if (item.getVip() == null || item.getVip().trim().isEmpty() || item.getVip().startsWith("#"))
            {
            	boolean haEnabled = false;
            	try
            	{
            		haEnabled = Boolean.parseBoolean(item.getHaEnabled());
            	}
            	catch (Exception e){}
            	
            	if (!haEnabled)
            	{
            		// VIP is nice to have, but not required for solo xBRCs
            		xbrc = "" + item.getIp() + ":" + item.getPort();
            	}
            	else
            	{
            		// HA enabled and no VIP is an illegal state
            		this.execStatus = 1;
                    this.execResult = "Reader not assigned. This xbrc is HA enabled but VIP is not configured for it.";

                    if(logger.isInfoEnabled()) {
                        logger.info("Failed to assigning readers (IDs: " + this.readerMacs + ") to xbrc " 
                        		+ item.getHostname() + ". This xbrc is HA enabled but doesn't have a VIP.");
                    }

                    return SUCCESS;
            	}
            }
            else
            {
            	xbrc = "" + item.getVip();
            }
        }
        else 
        {
        	// xbrc's address (hopefully VIP) typed in by user
            xbrc = this.xbrcVip;
        }

        if(logger.isInfoEnabled()) {
            logger.info("Assigning readers (IDs: " + this.readerMacs + ") via xbrms " +
            		XbrmsServerChooser.getInstance().getActiveXbrms(ServletActionContext.getRequest()).getUrl() + " to XBRC " + xbrc);
        }

        final boolean[] results = new boolean[macs.length];
        boolean result = true;
        final ReaderInfo[] readers = new ReaderInfo[macs.length];

        for(int i = 0; i < macs.length; ++i) {

            try {
                readers[i] = XbrmsUtils.getRestCaller().getUnassignedReaderByMac(macs[i]);
                
                int eRes = -1;
                try
                {
                	eRes = caller.assignReader(xbrc, macs[i]);
                	results[i] = eRes == 200 ? true : false;
                }
                catch (IllegalStateException ise)
                {
                	results[i] = false;
                }
                
                result = result && results[i];
            }
            catch (Exception e) {
                this.addActionError(e.getMessage());
                logger.warn(e.getMessage());
                result = false;
            }
        }
        
        if(result) {
            this.execResult = "Success";
            this.execStatus = 0;
        }
        else {
            this.execResult = "Failed to assign these readers: ";

            boolean first = true;

            for(int i = 0; i < results.length; ++i) {
                if(readers[i] == null) {
                    continue;
                }

                if(!results[i]) {
                    if(!first) {
                        this.execResult += ", ";
                    }

                    this.execResult += readers[i].getMacAddress();
                    first = false;
                }
            }

            if(!first) {
                this.execStatus = 2;
            }
            else {
                this.execStatus = 0;
                this.execResult = "";
            }
        }

        this.doPrepare();

        return SUCCESS;
    }
    
    public String identifyReader() throws Exception {
		
		if (macAddress == null || macAddress.trim().isEmpty()) {
			this.errMsg = "invalid request. Missing reader's MAC address.";
			return INPUT;
		}
		
        try {
            XbrmsUtils.getRestCaller().identifyReaderComp(PkConstants.XBRMS_KEY, this.macAddress);
        }
        catch (HTTPException e)
		{
			String excMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : null;
			
			switch (e.getStatusCode())
			{
				case 404: this.errMsg = "reader not found."; break;
				case 501: this.errMsg = "reader type with no light to light up."; break;
				default: this.errMsg = "HTTP status code: " + e.getStatusCode();
			}
			
			if (logger.isDebugEnabled())
				logger.error("Failed attempt to light up a reader " + macAddress
						+ ". Cause: " + excMessage == null ? " unknown" : " " + excMessage, e);
			else
				logger.error("Failed attempt to light up a reader " + macAddress
						+ ". Cause: " + excMessage == null ? " unknown" : " " + excMessage);
			
			return ERROR;
		}
        catch (Exception e) 
        {
        	logger.error("Exception while trying to light up a reader.", e);
            this.errMsg = e.toString();
            return ERROR;
        }

        return SUCCESS;
	}

	public List<ReaderInfo> getInventory() {
        return this.inventory;
	}
	
	public int getNumReaders()
	{
		if (inventory == null || inventory.size() <= 0)
			return 0;
		
		return inventory.size();
	}

    public String getExecResult() {
        return this.execResult;
    }

    public void setExecResult(String execResult) {
        this.execResult = execResult;
    }

    public int getExecStatus() {
        return this.execStatus;
    }

    public void setExecStatus(int execStatus) {
        this.execStatus = execStatus;
    }

    public Map<Long, String> getXbrcs() {
        return this.xbrcs;
    }

    public int getXbrcId() {
        return this.xbrcId;
    }

    public void setXbrcId(int xbrcId) {
        this.xbrcId = xbrcId;
    }

    public String getReaderMacs() {
        return readerMacs;
    }

    public void setReaderMacs(String readerMacs) {
        this.readerMacs = readerMacs;
    }
    
    public void setMacAddress(String macAddress){
    	this.macAddress = macAddress;
    }
    public String getMacAddress(){
    	return macAddress;
    }
    
    private void doPrepare() throws Exception {
        try {
            final IRestCall caller = XbrmsUtils.getRestCaller(
            		XbrmsServerChooser.getInstance().getActiveXbrms(ServletActionContext.getRequest()).getUrl());
            
            ReaderInfoListDto unassignedReaders = caller.getUnassignedReaders();
            if (unassignedReaders == null)
            	return;
            
            this.inventory = unassignedReaders.getReaderInfoList();
        }
        catch (XbrmsServerNotSetException e) {
        	logger.error("This exception should have been handled by the IsServerSelected interceptor.", e);
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            this.addActionError(e.getMessage());
        }

        this.xbrcs = new HashMap<Long, String>();
        this.dtos = new HashMap<Long, XbrcDto>();
        final Map<String, String> xbrcsTmp = new HashMap<String, String>();
        final Collection<XbrmsDto> xbrmss = this.getConfiguredXbrmss(true);

        if (xbrmss != null) {
            for (XbrmsDto xbrms : xbrmss) {
                if(xbrms.isGlobal()) {
                    continue;
                }

                if (!XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest())) {
                    if (XbrmsServerChooser.getInstance().getActiveXbrms(ServletActionContext.getRequest()).getId() != xbrms.getId()) {
                        continue;
                    }
                }

                try {
                    final IRestCall caller = XbrmsUtils.getRestCaller(xbrms.getUrl());
                    final List<XbrcDto> xbrcs = caller.getFacilities().getFacility();

                    if (xbrcs != null) {
                        for (XbrcDto xbrc : xbrcs) {
                            if (!xbrc.isAlive()) {
                                continue;
                            }

                            if (HAStatusEnum.slave.name().equals(xbrc.getHaStatus())) {
                                continue;
                            }

                            if (!xbrc.isValidHaStatus()) {
                                continue;
                            }

                            final String addr = (XbrmsUtils.isEmpty(xbrc.getVip()) || xbrc.getVip().startsWith("#")) ?
                                    (XbrmsUtils.isEmpty(xbrc.getHostname()) ? xbrc.getIp() : xbrc.getHostname()) :
                                    xbrc.getVip();

                            final String val;

                            if (XbrmsServerChooser.getInstance().isGlobalXbrms(ServletActionContext.getRequest())) {
                                val = xbrms.getDesc() + " -> " + xbrc.getName() + "/" + addr;
                            }
                            else {
                                val = xbrc.getName() + "/" + addr;
                            }

                            if (xbrcsTmp.get(val) == null) {
                                this.xbrcs.put(xbrms.getId() * ID_GAP + xbrc.getId(), val);
                                this.dtos.put(xbrms.getId() * ID_GAP + xbrc.getId(), xbrc);
                                xbrcsTmp.put(val, "");
                            }
                        }
                    }
                }
                catch (Exception e) 
                {
                	String errorMessage = "Failed to communicate with xBRMS server at " + xbrms.getUrl();
                    logger.warn(errorMessage, e);
                    this.addActionMessage(errorMessage);
                }
            }
        }

        this.xbrcs = XbrmsUtils.sortByValue(this.xbrcs);
    }

    public String getXbrcVip() {
        return xbrcVip;
    }

    public void setXbrcVip(String xbrcVip) {
        this.xbrcVip = xbrcVip;
    }
}
