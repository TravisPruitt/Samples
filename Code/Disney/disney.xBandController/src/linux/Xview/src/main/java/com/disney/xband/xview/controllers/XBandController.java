package com.disney.xband.xview.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.disney.xband.xview.Utils;
import com.disney.xband.xview.lib.model.*;

// XBand Supports.
public class XBandController extends baseController implements IController
{
	@Context
	UriInfo uriInfo;
	
	@Context
	Request request;
	
	public enum BandIdType
	{
		XBandId,
		BandId,
		LRId,
		TapId,
		SecureId,
		All
	}
	

	private static String generateSelectSQL(String id, BandIdType bandIdType)
	{

		String sql = "Select * from xband";
		
		switch (bandIdType)
		{
			case XBandId:
				sql += " where xbandId=? " + id;
				break;
			case BandId:
				sql += " where bandId=? ";
				break;
			case LRId:
				sql += " where lrid=? ";
				break;
			case TapId:
				sql += " where tapId=? ";
				break;
			case SecureId:
				sql += " where secureId=? ";
                break;
            default:
                break;
		}

		return sql;
		
	}
	
	
	private static Guest GetXBandGuest(String id) throws Exception
	{
		// Given an XBand Id, get the guest associated with it.
		Guest guest = new Guest();
		
		String sql = "Select guest.guestId, guest.lastName, guest.firstName, guest.xBMSId, guest.birthdate, guest.address1, guest.address2, guest.city, guest.state, guest.zip, guest.countryCode, guest.active, guest.createdBy, guest.createdDate, guest.updatedBy, guest.updatedDate from  guest Join  guest_xband on  guest.guestId =  guest_xband.guestId where  guest_xband.xbandId=?";
		
		List<baseModel> model = getResultSetAndClose(sql, guest, id);
		
		if (!model.isEmpty())
		{
			guest = (Guest)model.get(0);
		}
		else
		{
			guest = null;
		}
		
		return guest;
		
	}
	
	
	
	public static Xband GetXBandByXBandId(String id) throws Exception
	{
		
		return GetXBandById(id, BandIdType.XBandId);
	}
	
	public static Xband GetXBandByLRId(String id) throws Exception
	{
		
		return GetXBandById(id, BandIdType.LRId);
	}
	
	public static Xband GetXBandByBandId(String id) throws Exception
	{
		
		return GetXBandById(id, BandIdType.BandId);
	}
	
	public static Xband GetXBandByTapId(String id) throws Exception
	{
		
		return GetXBandById(id, BandIdType.TapId);
	}
	
	public static Xband GetXBandBySecureId(String id) throws Exception
	{
		return GetXBandById(id, BandIdType.SecureId);
	}
	
	
	public static Xband GetXBandById(String id, BandIdType bandIdType) throws Exception
	{
		String sql = generateSelectSQL(id, bandIdType);
		Xband xband = new Xband();

		List<baseModel> model = sql.indexOf("?") > 0 ? getResultSetAndClose(sql, xband, id) : getResultSetAndClose(sql, xband);
		
		if (!model.isEmpty())
		{
			xband = (Xband) model.get(0);
			
			Guest guest = GetXBandGuest(xband.getXBandId());
			if (guest != null)
			{
				guest.setGuestInfos(GuestInfoController.GetGuestInfosForGuest(guest.getGuestId()));
			}
			
			List<Guest> guests = new ArrayList<Guest>();
			guests.add(guest);
			
			xband.setGuests(guests);
			
		}
		else
		{
			xband = null;
		}
		
		
		
		return xband;
	}
	
	
	public static boolean SaveXBand(Xband xband)
	{
		return false;
	}
	
	// Create a new xBand in the system.
		public static Xband CreateXBand(Xband xband, String createdBy) throws Exception
		{
		
			Xband newBand = null;
			
			String dateTimeNow = Utils.getDateTimeNow();
			
			String sql = "INSERT INTO  xband " +
					"(" +
					" bandId," +
					" lRId," +
					" tapId," +
					" bandFriendlyName," +
					" printedName," +
					" active," +
					" createdBy," +
					" createdDate" +
					") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
					
				boolean result = ExecuteSQL(
                        sql,
                        xband.getBandId(), xband.getLRId(), xband.getTapId(), xband.getBandFriendlyName(),
                        xband.getPrintedName(), xband.getActive() + "", createdBy, dateTimeNow
                );
			
				if (result == true)
				{
					newBand = GetXBandByBandId(xband.getBandId().toString());
				}
				
				return newBand;	
		}
	
	public static boolean DeleteXBand(Xband xband) throws Exception
	{
		String sql = "DELETE FROM  xband where xbandId=?";
		
		boolean result = ExecuteSQL(sql, xband.getXBandId());
		
		return result;
	}
	
	public static boolean DeleteXBandById(String xbandId) throws Exception
	{
		String sql = "DELETE FROM  xband where xbandId=?";
		
		boolean result = ExecuteSQL(sql, xbandId);
		
		return result;
	}
	
	public static boolean DeleteAllXBandsCreatedBy(String createdBy) throws Exception
	{
		String sql = "DELETE FROM xband where createdBy=?";
		
		boolean result = ExecuteSQL(sql, createdBy);
		
		return result;
	}
	
	// This GetAll allows the selection of XBands by the createdBy.  This will allow
	// the system to filter out simulators or Demos or whatever.
	public static List<Xband>GetAllXBands(String createdBy) throws Exception
	{
		String sql = "Select * from xband where createdBy=?";
		
		Xband xband = new Xband();
		List<Xband> xbands = new ArrayList<Xband>();
		
		List<baseModel> models = getResultSetAndClose(sql, xband, createdBy);
				
		for(baseModel model : models)
		{
			xband = (Xband)model;
			
			Guest guest = GetXBandGuest(xband.getXBandId());
			
			List<Guest> guests = new ArrayList<Guest>();
			guests.add(guest);
			
			xband.setGuests(guests);
			
			xbands.add(xband);
		}
		
		
		return xbands;
		
	}
	
	public static List<Xband> GetAllXBands() throws Exception
	{
		String sql = "Select * from  xband";
		
		Xband xband = new Xband();
		List<Xband> xbands = new ArrayList<Xband>();
		
		List<baseModel> models = getResultSetAndClose(sql, xband);
				
		for(baseModel model : models)
		{
			xband = (Xband)model;
			
			Guest guest = GetXBandGuest(xband.getXBandId());
			
			List<Guest> guests = new ArrayList<Guest>();
			guests.add(guest);
			
			xband.setGuests(guests);
			
			xbands.add(xband);
		}
		
		
		return xbands;
	}
	
	private static boolean ActivateXBand(String xBandId) throws Exception
	{
		String sql = "Update xband SET active=1 WHERE xBandId=?";
		
		boolean result = ExecuteSQL(sql, xBandId);
		
		return result;
	}
	
	private static boolean DeactivateXBand(String xBandId) throws Exception
	{
		String sql = "Update xband SET active=0 WHERE xBandId=?";
		
		boolean result = ExecuteSQL(sql, xBandId);
		
		return result;
	}
	
	public static boolean setXBandActiveState(String xBandId, boolean active) throws Exception
	{
		boolean result = false;
		
		if (active == true)
		{
			result = ActivateXBand(xBandId);
		}
		else
		{
			result = DeactivateXBand(xBandId);
		}
		
		return result;
	}

}
