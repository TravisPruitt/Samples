package com.disney.xband.xbrms.client.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.disney.xband.common.lib.audit.Auditor;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class AuditInterceptor implements Interceptor
{

	@Override
	public void destroy() {}

	@Override
	public void init() {}

	@Override
	public String intercept(ActionInvocation action) throws Exception
	{
		final HttpServletRequest request = ServletActionContext.getRequest();
		
		final AuditConfig auditConfig = Auditor.getInstance().getConfig();

        if(auditConfig.isEnabled()) 
        {    
            auditConfig.setvHost(request.getServerName());
            auditConfig.setHost(request.getLocalName());
        }

        return action.invoke();
	}
}
