package com.disney.xband.xbrms.server.model;

import com.disney.xband.xbrms.common.model.IDataTransferObject;
import com.disney.xband.xbrms.common.model.XbrmsConfigDto;

public class XbrmsConfigBo implements IBusinessObject {
	private XbrmsConfigDto dto;
	
	public static XbrmsConfigBo getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private XbrmsConfigBo()
	{
		this.dto = new XbrmsConfigDto();
	}
    private static class SingletonHolder
	{
		public static final XbrmsConfigBo INSTANCE = new XbrmsConfigBo();
	}

    ///// IBusinessObject interface begin ////

    @Override
    public XbrmsConfigDto getDto() {
        return this.dto;
    }

    @Override
    public void setDto(IDataTransferObject dto) {
        this.dto = (XbrmsConfigDto) dto;
    }

    ///// IBusinessObject interface end ////
}
