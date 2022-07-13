package com.disney.xband.xfpe.simulate;

public class PEGuestAction {
	private Integer id;
	private Integer guestId;
	private Integer seq;
	private String desc;
	private PEGuestActionType type;
	private Integer delaySec;
	private Boolean fireAfterEvent;
	private String data;
	
	public PEGuestAction(Integer id, Integer guestId, Integer seq, String desc,
			PEGuestActionType type, Integer delaySec, Boolean fireAfterEvent,
			String data) {
		super();
		this.id = id;
		this.guestId = guestId;
		this.seq = seq;
		this.desc = desc;
		this.type = type;
		this.delaySec = delaySec;
		this.fireAfterEvent = fireAfterEvent;
		this.data = data;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getGuestId() {
		return guestId;
	}
	public void setGuestId(Integer guestId) {
		this.guestId = guestId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public PEGuestActionType getType() {
		return type;
	}
	public void setType(PEGuestActionType type) {
		this.type = type;
	}
	public Integer getDelaySec() {
		return delaySec;
	}
	public void setDelaySec(Integer delaySec) {
		this.delaySec = delaySec;
	}
	public Boolean getFireAfterEvent() {
		return fireAfterEvent;
	}
	public void setFireAfterEvent(Boolean fireAfterEvent) {
		this.fireAfterEvent = fireAfterEvent;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
