package com.disney.xband.xbrc.lib.entity;

public class CastMember {
	private Long id;
	private String name;
	private String bandId;
	private String externalId;
	private String omniUsername;
	private String omniPassword;
	private CastMemberTapAction tapAction;
	private boolean enabled;	
	
	public CastMember(){		
		tapAction = CastMemberTapAction.ClearBlueLane;	// default action
	}
	
	public CastMember(Long id, String name, String bandId, String externalId,
			String omniUsername, String omniPassword, CastMemberTapAction tapAction, boolean enabled) {
		super();
		this.id = id;
		this.name = name;
		this.bandId = bandId;
		this.externalId = externalId;
		this.omniUsername = omniUsername;
		this.omniPassword = omniPassword;
		this.tapAction = tapAction;
		this.enabled = enabled;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getOmniUsername() {
		return omniUsername;
	}
	public void setOmniUsername(String omniUsername) {
		this.omniUsername = omniUsername;
	}
	public String getOmniPassword() {
		return omniPassword;
	}
	public void setOmniPassword(String omniPassword) {
		this.omniPassword = omniPassword;
	}
	public CastMemberTapAction getTapAction() {
		return tapAction;
	}

	public void setTapAction(CastMemberTapAction tapAction) {
		this.tapAction = tapAction;
	}

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
