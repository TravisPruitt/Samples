package com.disney.xband.xbrms.client.interceptor;

import org.apache.struts2.ServletActionContext;

import com.disney.xband.xbrms.client.XbrmsServerChooser;
import com.disney.xband.xbrms.client.XbrmsServerNotSetException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class IsServerSelectedInterceptor implements Interceptor
{

	@Override
	public void destroy() {}

	@Override
	public void init() {}

	@Override
	public String intercept(ActionInvocation action) throws Exception
	{
		try
        {
			XbrmsServerChooser.getInstance().getServerUri(ServletActionContext.getRequest());
        }
        catch (XbrmsServerNotSetException e)
        {
    		return "serverNotSet";
        }
		
		return action.invoke();
	}

}
