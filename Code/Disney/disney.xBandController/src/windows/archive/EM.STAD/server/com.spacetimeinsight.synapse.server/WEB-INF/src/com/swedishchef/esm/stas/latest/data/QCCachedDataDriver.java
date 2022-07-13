package com.swedishchef.esm.stas.latest.data;

import java.io.Serializable;
import java.util.List;

import com.enterprisehorizons.magma.datamashup.BaseGeoDataDriver;
import com.swedishchef.esm.stas.latest.data.QCCachedDataSource;

public class QCCachedDataDriver extends BaseGeoDataDriver implements Serializable {
	com.swedishchef.esm.stas.latest.data.QCCachedDataSource cachedDataSource;
	public QCCachedDataDriver(QCCachedDataSource datasource) {
		super(datasource);
		this.cachedDataSource = datasource;
	}

	public List getData() 
	{
		if(cachedDataSource.getType().equals("QueueCountArtifacts"))
			return DataManager.getInstance().attractionLookup(cachedDataSource.getAttractionName());
		else if(cachedDataSource.getType().equals("Parks"))
			return DataManager.getInstance().getParks();
		return DataManager.getInstance().returnAttractionArtifacts();
	}
}
