package com.disney.xband.xbrms.client.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.xbrms.client.XbrmsAccessHelper;
import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class DenyMaintenanceRoleInterceptor implements Interceptor
{

	@Override
	public void destroy() {}

	@Override
	public void init() {}

	@Override
	public String intercept(ActionInvocation action) throws Exception
	{
		final HttpServletRequest request = ServletActionContext.getRequest();
        
        boolean redirectToGlobal = false;
        try
        {
			redirectToGlobal = (!XbrmsAccessHelper.getInstance().canAccessAsset("Deny maintenance role", request) && 
					!XbrmsServerChooser.getInstance().isGlobalXbrms(request));
        }
        catch (XbrmsServerNotSetException e)
        {
        	redirectToGlobal = true;
        }
        
        if (redirectToGlobal)
        {
        	ServletActionContext.getResponse().sendRedirect(XbrmsServerChooser.getInstance().getGlobalServerLink(request));
        }

        return action.invoke();
	}
}
