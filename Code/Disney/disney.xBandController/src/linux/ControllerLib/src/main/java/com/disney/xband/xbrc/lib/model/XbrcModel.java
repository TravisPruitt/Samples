package com.disney.xband.xbrc.lib.model;

public enum XbrcModel {
	ATTRACTION("attractionmodel", "com.disney.xband.xbrc.attractionmodel.CEP"),		//0
	PARKENTRY("parkentrymodel", "com.disney.xband.xbrc.parkentrymodel.CEP"),		//1
	SPACE("spacemodel", "com.disney.xband.xbrc.spacemodel.CEP");					//2
	
	private String model;
	private String xbrcModelClass;
	
	XbrcModel(String model, String xbrcModelClass){
		this.model = model;
		this.xbrcModelClass = xbrcModelClass;
	}
	
	public static XbrcModel getByModel(String model)
	{
		if (model == null)
			return null;
		
		String m = model.toLowerCase();
		
		if (m.contains("attraction"))
			return XbrcModel.ATTRACTION;
		if (m.contains("parkentry"))
			return XbrcModel.PARKENTRY;
		if (m.contains("space"))
			return XbrcModel.SPACE;
	
		return null;
	}
	
    public static String getShortModel(String model)
    {
        if (model == null)
            return null;

        String m = model.toLowerCase();

        if (m.contains("attraction"))
            return XbrcModel.ATTRACTION.model;
        if (m.contains("parkentry"))
            return XbrcModel.PARKENTRY.model;
        if (m.contains("space"))
            return XbrcModel.SPACE.model;

        return null;
    }

	public static String getLongModel(String model)
	{
		XbrcModel modelEnum = getByModel(model);
		
		if (modelEnum == null)
			return null;
		
		return modelEnum.getXbrcModelClass();
	}
	
	public void setModel(String model){
		this.model = model;
	}
	
	public String getModel(){
		return model;
	}
	
	public String getXbrcModelClass(){
		return xbrcModelClass;
	}
	
	public void setXbrcModelClass(String xbrcModelClass){
		this.xbrcModelClass = xbrcModelClass;
	}
}
