package com.disney.xband.idms.dao;

import com.disney.xband.idms.lib.data.BandData;
import com.disney.xband.idms.lib.model.XBandCollection;
import com.disney.xband.idms.lib.model.XBandPut;
import com.disney.xband.idms.lib.model.XbandAssociation;


public interface XBandDAO 
{
	public BandData GetBand(BandLookupType bandLookupType, String id) throws DAOException;
		
	public long Create(XBandPut xband) throws DAOException;

	public long Create(XbandAssociation xbandAssociation) throws DAOException;

	public XBandCollection GetXBands(String identifierType, String identifierValue) throws DAOException;

	public boolean AssignXbandToGuest(long guestId, long xbandId) throws DAOException;
		
	public boolean AssignXbandToGuest(String identifierType, String identifierValue, long xbandId) throws DAOException;

	public boolean UnassignXBand(long guestId, long xbandId) throws DAOException;
	
	public boolean ReassignXBand(long guestId, String xbmsId) throws DAOException;

	public boolean UpdateType(long xbandId, String newTypeName) throws DAOException;

	public boolean Update(long xbandId, boolean active, String typeName) throws DAOException;
}
