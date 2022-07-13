package com.swedishchef.esm.stas.latest.data;

import com.enterprisehorizons.magma.datamashup.BaseGeoDataSource;

public class QCCachedDataSource extends BaseGeoDataSource { 	
	private String type;
	private String attractionName; 
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAttractionName() {
		return attractionName;
	}
	
	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}
}
