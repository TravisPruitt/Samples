package com.disney.xband.xbrms.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.disney.xband.xbrms.common.model.IDataTransferObject;
import com.disney.xband.xbrms.common.model.ProblemDto;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.model.ProblemsReportDto;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 9/24/12
 * Time: 2:14 PM
 */
public class ProblemsReportBo implements IBusinessObject {
    private ProblemsReportDto dto;

    private static class SingletonHolder {
		public static final ProblemsReportBo INSTANCE = new ProblemsReportBo();
	}

	public static ProblemsReportBo getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private ProblemsReportBo() {
        this.dto = new ProblemsReportDto();
        this.dto.setErrors(Collections.synchronizedMap(new HashMap<ProblemAreaType, ProblemDto>()));
    }

    public void setLastError(ProblemAreaType area, String errorMessage, Throwable error) {
        this.dto.addError(area, new ProblemDto(errorMessage, new Date(), error));
    }
    
    public void setLastError(ProblemAreaType area, String errorMessage) {
        this.dto.addError(area, new ProblemDto(errorMessage, new Date()));
    }
    
    public void setLastError(ProblemAreaType area, Throwable error) {
        this.dto.addError(area, new ProblemDto(new Date(), error));
    }

    public void clearLastError(ProblemAreaType area) {
        this.dto.getErrors().remove(area);
    }

    public List<ProblemDto> getErrors() {
        final List<ProblemDto> list = new ArrayList<ProblemDto>();
        final Iterator<ProblemDto> it = this.dto.getErrors().values().iterator();

        while(it.hasNext()) {
            list.add(it.next());
        }

        return list;
    }
    
    public ProblemDto getError(ProblemAreaType errorType)
    {
    	return this.dto.getError(errorType);
    }

    public List<ProblemAreaType> getErrorAreas() {
        final List<ProblemAreaType> list = new ArrayList<ProblemAreaType>();
        final Iterator<ProblemAreaType> it = this.dto.getErrors().keySet().iterator();

        while(it.hasNext()) {
            list.add(it.next());
        }

        return list;
    }

    public void clearErrors() {
        this.dto.getErrors().clear();
    }


    ///// IBusinessObject interface begin ////

    @Override
    public ProblemsReportDto getDto() {
        return this.dto;
    }

    @Override
    public void setDto(IDataTransferObject dto) {
        this.dto = (ProblemsReportDto) dto;
    }

    ///// IBusinessObject interface end ////
}
