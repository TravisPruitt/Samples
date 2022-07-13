package com.disney.xband.xbrms.client.action;

import java.util.ArrayList;
import java.util.List;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.ProblemDto;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

public class XbrmsStatusAction extends BaseAction {
	
    private List<ProblemDto> problems;

	@Override
	public String execute() throws Exception {
        this.problems = new ArrayList<ProblemDto> (XbrmsUtils.getRestCaller().getRecentXbrmsProblems().getErrors().values());

		return SUCCESS;
	}

    public String clear() {
        XbrmsUtils.getRestCaller().clearXbrmsProblemsList();
        this.problems = new ArrayList<ProblemDto>(XbrmsUtils.getRestCaller().getRecentXbrmsProblems().getErrors().values());

        return SUCCESS;
    }

    public List<ProblemDto> getProblems() {
        return this.problems;
    }
}
