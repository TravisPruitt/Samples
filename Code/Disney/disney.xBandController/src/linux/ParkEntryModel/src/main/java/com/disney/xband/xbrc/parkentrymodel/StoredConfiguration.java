package com.disney.xband.xbrc.parkentrymodel;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.xbrc.lib.db.CastMemberService;
import com.disney.xband.xbrc.lib.db.OmniServerService;
import com.disney.xband.xbrc.lib.db.ReaderOmniServerService;
import com.disney.xband.xbrc.lib.entity.CastMember;
import com.disney.xband.xbrc.lib.entity.OmniServer;
import com.disney.xband.xbrc.lib.entity.ParkEntryModelConfig;
import com.disney.xband.xbrc.lib.entity.ReaderOmniServer;
import com.disney.xband.xbrc.lib.entity.ReaderOmniServers;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class StoredConfiguration
{
	/*
	 * Return configuration in xml string.
	 */
	public static String storeConfiguration(int indent) throws Exception
	{
		Connection conn = null;
		try 
		{
			conn = XBRCController.getInstance().getPooledConnection();
			ParkEntryModelConfig modelConfig = new ParkEntryModelConfig();
			modelConfig.setReaderOmniServers(getReaderOmniServers(conn));
			modelConfig.setCastMembers(getCastMembers(conn));
			String xml = XmlUtil.convertToPartialXml(modelConfig, ParkEntryModelConfig.class);
			return xml;
		}
		finally
		{
			if (conn != null)
				XBRCController.getInstance().releasePooledConnection(conn);
		}		
	}
	
	private static ReaderOmniServers getReaderOmniServers(Connection conn) throws Exception
	{			
		ReaderOmniServers os = new ReaderOmniServers();
		os.setReaderOmniServers(ReaderOmniServerService.getAll(conn));
		os.setOmniServers(OmniServerService.getAll(conn));
		return os;
	}
	
	private static Collection<CastMember> getCastMembers(Connection conn) throws SQLException
	{
		Collection<CastMember> castMemberList = CastMemberService.findAll(conn);
		return castMemberList;
	}
	
	/*
	 * Save configuration to the database.
	 */
	public static void restoreConfiguration(Connection conn, String xml) throws Exception
	{      			
    	ParkEntryModelConfig modelConfig = XmlUtil.convertToPojo(new ByteArrayInputStream(xml.getBytes()), ParkEntryModelConfig.class);
    	if (modelConfig == null)
    		return;
        if (modelConfig.getReaderOmniServers() != null)
        	saveReaderOmniServers(conn, modelConfig.getReaderOmniServers());    
        if (modelConfig.getCastMembers() != null)
        	CastMemberService.insertAll(conn, modelConfig.getCastMembers(), true);
	}
		
	
	private static void saveReaderOmniServers(Connection conn, ReaderOmniServers ros)
			throws Exception
	{
		if (ros.getOmniServers() == null)
			return;
		
		for (OmniServer os : ros.getOmniServers())
		{
			ReaderOmniServerService.deleteByOmniId(conn, os.getId());
			OmniServerService.delete(conn, os.getId(), false);
			OmniServerService.save(conn, os);
		}
		
		if (ros.getReaderOmniServers() == null)
			return;
		
		for (ReaderOmniServer e : ros.getReaderOmniServers())
		{
			ReaderOmniServerService.deleteByReaderAndOmniId(conn, e.getReaderid(), e.getOmniserverid());
			ReaderOmniServerService.save(conn, e);
		}
	}
}
