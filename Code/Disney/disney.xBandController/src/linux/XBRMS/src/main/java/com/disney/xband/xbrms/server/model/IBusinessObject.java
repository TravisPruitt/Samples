package com.disney.xband.xbrms.server.model;

import com.disney.xband.xbrms.common.model.IDataTransferObject;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/2/13
 * Time: 5:18 PM
 */
public interface IBusinessObject<T extends IDataTransferObject> {
	T getDto();
    void setDto(T dto);
}
