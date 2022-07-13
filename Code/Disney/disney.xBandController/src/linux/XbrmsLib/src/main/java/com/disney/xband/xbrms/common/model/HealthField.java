package com.disney.xband.xbrms.common.model;

import java.lang.reflect.Method;

public class HealthField
{
	private IHealthField desc;
	private Method method;
	
	public IHealthField getDesc()
	{
		return desc;
	}
	public void setDesc(IHealthField desc)
	{
		this.desc = desc;
	}
	public Method getMethod()
	{
		return method;
	}
	public void setMethod(Method method)
	{
		this.method = method;
	}
}
