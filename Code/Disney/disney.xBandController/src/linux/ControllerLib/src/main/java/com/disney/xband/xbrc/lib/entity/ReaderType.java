package com.disney.xband.xbrc.lib.entity;

/**
 * Any changes to this enumeration will require changes to the DISNEY-XBRC-MIB
 */
public enum ReaderType {
	undefined("Undefined", "", false),					//0
	lrr("Long Range", "Long Range", false),				//1
	xfp("Tap", "xFP", true),							//2
	xfpxbio("Tap + xBio", "xFP+xBIO", true),			//3
	mobileGxp("Mobile Gxp", "Mobile Gxp", false),		//4
	vid("Vehicle ID", "VID", false),					//5
	xtpra("xTPra", "xTPra", false);						//6
	
	private String description;
	private String type;
	private boolean canProcessSecureId;
	
	ReaderType(String description, String type, boolean canProcessSecureId){
		this.description = description;
		this.type = type;
		this.setCanProcessSecureId(canProcessSecureId);
	}
	
	public static boolean isLongRangeReader(ReaderType rtyp)
	{
		return rtyp==ReaderType.lrr;
	}
	
	public static boolean isTapReader(ReaderType rtyp)
	{
		// We can tap on any of these
		return rtyp.equals(ReaderType.mobileGxp) ||
				rtyp.equals(ReaderType.xfp) ||
				rtyp.equals(ReaderType.xfpxbio);
	}
	
	public static boolean supportsMultipleStreams(ReaderType rtyp)
	{
		return rtyp.equals(ReaderType.xfp) ||
				rtyp.equals(ReaderType.xfpxbio) ||
				rtyp.equals(ReaderType.xtpra);
	}
	
	/**
	 * Needed by the UI.
	 * @return
	 */
	public boolean hasLight()
	{
		return isTapReader(this) && this!=ReaderType.mobileGxp;
	}
	/**
	 * Needed by the UI.
	 * @return
	 */
	public boolean isLongRange()
	{
		return isLongRangeReader(this);
	}
	
	public static ReaderType getByOrdinal(int ordinal){
		
		switch(ordinal){
			case 0: return ReaderType.undefined;
			case 1: return ReaderType.lrr;
			case 2: return ReaderType.xfp;
			case 3: return ReaderType.xfpxbio;
			case 4: return ReaderType.mobileGxp;
			case 5: return ReaderType.vid;
			case 6: return ReaderType.xtpra;
			default: return null;
		}
	}
	
	public static ReaderType getByType(String type){
		if (type == null || type.trim().isEmpty())
			return ReaderType.undefined;
		
		if (type.equals(lrr.type))
			return lrr;
		else if (type.equals(xfp.type))
			return xfp;
		else if (type.equals(xfpxbio.type))
			return xfpxbio;
		else if (type.equals(mobileGxp.type))
			return mobileGxp;
		else if (type.equals(vid.type))
			return vid;
		else if (type.equals("AVMS Vehicle")) // for backwards compatibility of stored configurations
			return vid;
		else if (type.equals(xtpra.type))
			return xtpra;
		else
			return ReaderType.undefined;
	}
	
	@Deprecated
	public static ReaderType getByDescription(String description){
		if (description == null || description.trim().isEmpty())
			return ReaderType.undefined;
		
		if (description.equals(lrr.description))
			return lrr;
		else if (description.equals(xfp.description))
			return xfp;
		else if (description.equals(xfpxbio.description))
			return xfpxbio;
		else if (description.equals(mobileGxp.description))
			return mobileGxp;
		else if (description.equals(vid.description))
			return vid;
		else if (description.equals("AVMS Vehicle")) // for backwards compatibility of stored configurations
			return vid;
		else if (description.equals(xtpra.description))
			return xtpra;
		else
			return ReaderType.undefined;
	}
	
	public static ReaderType getByInstallerName(String name) {
		if (name.equalsIgnoreCase("xbr"))
			return lrr;
		else if (name.equalsIgnoreCase("xfp") || name.equalsIgnoreCase("xtp") || name.equals("xtpra"))
			return xfp;
		else if (name.equalsIgnoreCase("xfpxbio") || name.equalsIgnoreCase("xtpxbio"))
			return xfpxbio;
		else
			return undefined;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isCanProcessSecureId()
	{
		return canProcessSecureId;
	}

	public void setCanProcessSecureId(boolean canProcessSecureId)
	{
		this.canProcessSecureId = canProcessSecureId;
	}
	
	public boolean getHasLight()
	{
		return this.hasLight();
	}
}
