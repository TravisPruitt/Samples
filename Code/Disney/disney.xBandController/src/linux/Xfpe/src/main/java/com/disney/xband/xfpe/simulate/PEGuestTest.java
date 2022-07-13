package com.disney.xband.xfpe.simulate;

import java.util.List;

import com.disney.xband.xfpe.model.XfpeReader;

public class PEGuestTest {
	private Integer id;
	private Long suiteId;
	private Integer seq;
	private String name;
	private String bandId;
	private Boolean child;
	private Boolean validBand;
	private String reason;
	private String bioTemplate;
	private Integer omniLevel;
	private PEGuestTestResult finalResult;
	private String desc;
	private List<PEGuestAction> actions;
	private XfpeReader reader;
	
	public PEGuestTest(Integer id, Long suiteId, Integer seq, String name, String bandId, Boolean child,
			Boolean validBand, String reason, String bioTemplate,
			Integer omniLevel, PEGuestTestResult finalResult, String desc) {
		super();
		this.id = id;
		this.suiteId = suiteId;
		this.seq = seq;
		this.name = name;
		this.bandId = bandId;
		this.child = child;
		this.validBand = validBand;
		this.reason = reason;
		this.bioTemplate = bioTemplate;
		this.omniLevel = omniLevel;
		this.finalResult = finalResult;
		this.desc = desc;
	}
	public List<PEGuestAction> getActions() {
		return actions;
	}
	public void setActions(List<PEGuestAction> actions) {
		this.actions = actions;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBandId() {
		return bandId;
	}
	public void setBandId(String bandId) {
		this.bandId = bandId;
	}
	public Boolean getChild() {
		return child;
	}
	public void setChild(Boolean child) {
		this.child = child;
	}
	public Boolean getValidBand() {
		return validBand;
	}
	public void setValidBand(Boolean validBand) {
		this.validBand = validBand;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getBioTemplate() {
		return bioTemplate;
	}
	public void setBioTemplate(String bioTemplate) {
		this.bioTemplate = bioTemplate;
	}
	public Integer getOmniLevel() {
		return omniLevel;
	}
	public void setOmniLevel(Integer omniLevel) {
		this.omniLevel = omniLevel;
	}
	public PEGuestTestResult getFinalResult() {
		return finalResult;
	}
	public void setFinalResult(PEGuestTestResult finalResult) {
		this.finalResult = finalResult;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public XfpeReader getReader() {
		return reader;
	}
	public void setReader(XfpeReader reader) {
		this.reader = reader;
	}
	public Long getSuiteId() {
		return suiteId;
	}
	public void setSuiteId(Long suiteId) {
		this.suiteId = suiteId;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
