package com.disney.xband.xbrms.common.model;

import com.disney.xband.common.lib.health.Status;

public interface IHealthSystem 
{
	HealthItemDto getDto();
	
	void setDto(HealthItemDto dto);
    
	public void update(HealthItemDto freshHealthItem);
	
	public Status refreshStatus();
}
