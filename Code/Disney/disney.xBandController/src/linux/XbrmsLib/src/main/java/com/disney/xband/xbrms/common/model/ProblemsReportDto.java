package com.disney.xband.xbrms.common.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 9/25/12
 * Time: 4:33 PM
 */
@XmlRootElement(name = "problemsReport")
public class ProblemsReportDto implements IDataTransferObject
{
    private Map<ProblemAreaType, ProblemDto> errors = new HashMap<ProblemAreaType, ProblemDto>();

    public Map<ProblemAreaType, ProblemDto> getErrors() {
        return errors;
    }
    
    public ProblemDto getError(ProblemAreaType errorType)
    {
    	return errors.get(errorType);
    }

    public void setErrors(Map<ProblemAreaType, ProblemDto> errors) {
        this.errors = errors;
    }
    
    public void addError(ProblemAreaType area, ProblemDto problem)
    {
    	this.errors.put(area, problem);
    }
}
