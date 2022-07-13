package com.disney.xband.xbrms.client;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.ac.lib.client.filter.XbPrincipal;

public class XbrmsAccessHelper
{
	private static Logger logger = Logger.getLogger(XbrmsAccessHelper.class);
	
	public static XbrmsAccessHelper getInstance()
	{
		return SingletonHolder.instance;
	}
	
	public boolean canAccessAsset(String assetId, HttpServletRequest req) {
        if(req.getAttribute("ReqAttrNameAcDisabled") != null) {
            return true;
        }

        final XbPrincipal user = (XbPrincipal) req.getUserPrincipal();

        if(user == null) {
            return false;
        }

        return user.canAccessAsset(assetId);
    }
	
	private static class SingletonHolder
	{
		public static final XbrmsAccessHelper instance = new XbrmsAccessHelper();
	}
}
