package com.disney.xband.xview.controllers;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.disney.xband.xview.Utils;
import com.disney.xband.xview.lib.model.*;

public class GuestController extends baseController {

	// Get a guest object by Id and return it.
	public static Guest GetGuestById(String guestId) throws Exception
	{
		
		String sqlQuery = "Select * from guest where guestId=?";
		
		Guest retVal = new Guest();
	   
	    
	    List<baseModel> result = getResultSetAndClose(sqlQuery, retVal, guestId);
	
	    
		if (result.isEmpty())
		{
			retVal = null;
		}
		else
		{
			retVal = (Guest) result.get(0);
			
			retVal.setXbands(GetGuestXBands(retVal.getGuestId()));
			retVal.setGuestInfos(GuestInfoController.GetGuestInfosForGuest(guestId));
		}
		
		return retVal;
	}
	
	
	public static List<Guest> SearchGuestName(String name) throws Exception
	{
		String sql ="Select * from guest where concat(firstName, ' ', lastName) like ? AND createdBy <> 'simulator'";

		Guest guest = new Guest();
		
		List<Guest> guests = new ArrayList<Guest>();
		
		List<baseModel> guestsBase = getResultSetAndClose(sql, guest, "%" + name + "%");
		
		if (!guestsBase.isEmpty())
		{
			for( baseModel model : guestsBase)
			{
				guest = (Guest)model;
				guest.setGuestInfos(GuestInfoController.GetGuestInfosForGuest(guest.getGuestId()));
				guest.setXbands(GetGuestXBands(guest.getGuestId()));
				guests.add(guest);
				
				System.out.println("added : " + ((Guest)model).getFirstName());
			}
		}
		else
		{
			System.out.println("Empty.");
		}
		
		
		return guests;
	}
	
	public static boolean UpdateGuest(Guest guest) throws Exception
	{
		boolean result = false;
		
		String sql = "UPDATE guest SET " +
				"firstName=?" +
				",lastName=?" +
				",address1=?" +
				",address2=?" +
				",city=?" +
				",countryCode=?" +
				",state=?" +
				",zip=?" +
				",birthdate=?" +
				",xBMSId=?" +
				",active=?" +
				",updatedBy=?" +
				",updatedDate=?" +
				" WHERE guestId=?";
		
		result = ExecuteSQL(
                sql,
                guest.getFirstName(), guest.getLastName(), guest.getAddress1(), guest.getAddress2(),
                guest.getCity(), guest.getCountryCode(), guest.getState(), guest.getZip(),
                guest.getBirthdate() + "", guest.getXBMSId(), guest.getActive() + "", Utils.getUserName(),
                Utils.getDateTimeNow(), guest.getGuestId()
        );
		
		return result;
	}
	
	// Create a new guest object in the database.
	public static Guest CreateGuest(Guest guest) throws Exception
	{	
		String dateTimeNow = Utils.getDateTimeNow();
		
		String createdBy = guest.getCreatedBy();
		
		if (createdBy == "" || createdBy == "null" || createdBy == null)
		{
			createdBy = Utils.getUserName();
		}

		String sql = "INSERT INTO guest " +
				      "(" +
				      "lastName," +
				      "firstName," +
				      "address1," +
				      "address2," +
				      "city," +
				      "state," +
				      "zip," +
				      "countryCode," +
				      //"birthDate," +
				      "xBMSId," +
				      "active," +
				      "createdBy," +
				      "createdDate" +
				      ") VALUES " +
				      "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		boolean result = ExecuteSQL(
                sql, guest.getLastName(), guest.getFirstName(), guest.getAddress1(), guest.getAddress2(),
                guest.getCity(), guest.getState(), guest.getZip(), guest.getCountryCode(), guest.getXBMSId(),
                guest.getActive() + "", createdBy, dateTimeNow
                );
		// Now try to find the result.
		
		if (result == true)
		{
			sql = "SELECT * FROM guest WHERE createdDate=? AND lastName=? AND firstName=?";
			
			Guest retVal = new Guest();
			
			List<baseModel> findGuest =  getResultSetAndClose(sql, retVal, dateTimeNow, guest.getLastName(), guest.getFirstName());
			
			if (!findGuest.isEmpty())
			{
				guest = (Guest)findGuest.get(0);
			}
		}
	    // After successful creation, return the populated guest object back to the caller.
		return guest;
	}
	
	// Delete a guest object from the database.
	public static boolean DeleteGuest(Guest guest)
	{
		return false;
	}
	
	// Return an ArrayList of all guests in the database.  Maybe should make this a list of active only?
	public static List<Guest> GetAllGuests() throws Exception
	{
		String sqlQuery = "Select * from guest where createdBy <> 'simulator'"; 
		
		List<Guest> guests = new ArrayList<Guest>();
		
		Guest guest = new Guest();
		
		List<baseModel> result = getResultSetAndClose(sqlQuery, guest);
		 

		for(baseModel model : result)
		{
			Guest g = (Guest) model;
			g.setXbands(GetGuestXBands(g.getGuestId()));
			g.setGuestInfos(GuestInfoController.GetGuestInfosForGuest(g.getGuestId()));
			
			guests.add(g);
		}
		
		return guests;
	}
	
	// Get only the XBands for a guest.
	public static List<Xband> GetGuestXBands(String id) throws Exception
	{
		String sql = "Select xband.xBandId, xband.bandId, xband.lRId, xband.tapId, xband.active, xband.bandfriendlyname, xband.printedName, xband.createdBy, xband.createdDate, xband.updatedBy, xband.updatedDate from xband join guest_xband on guest_xband.xbandId = xband.xbandId where guest_xband.guestId=?";
		
		Xband xband = new Xband();
		
		List<baseModel> result = getResultSetAndClose(sql, xband, id);
		
		List<Xband> addBands = new ArrayList<Xband>();
		
		for (baseModel model : result)
		{
			addBands.add((Xband)model);
		}
		
		//List<Xband> retVal = new ArrayList<Xband>();
	
		return addBands;
	}
	
	public static boolean AddXBandToGuest(String guestId, String xBandId) throws Exception
	{
		boolean retVal = false;
		// We have to first check to see if a band has been assigned to a guest.
		// We will delete the old association.
			
		String sql = "DELETE FROM guest_xband WHERE guestId=? AND xBandId=?";
		
		retVal = ExecuteSQL(sql, guestId, xBandId);
		
		if (retVal == true)
		{
			Date now = new Date();
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String dtNow = df.format(now);
			
			sql = "INSERT INTO guest_xband (guestId, xBandId, createdBy, createdDate) VALUES (?, ?, ?, ?)";
			
			System.out.println("SQL : " + sql);
						
			retVal = ExecuteSQL(sql, guestId, xBandId, Utils.getUserName(), dtNow);
			
			// Mark the xBand active.
			if (retVal == true)
			{
				retVal = XBandController.setXBandActiveState(xBandId, true);
			}
		}

		return retVal;
	}
	
	
	
	
	public static boolean RemoveXBandFromGuest(String guestId, String xBandId) throws Exception
	{
		boolean retVal = false;
		// We have to first check to see if a band has been assigned to a guest.
		// We will delete the old association.
			
		String sql = "DELETE FROM guest_xband WHERE guestId=? AND xBandId=?";
		
		retVal = ExecuteSQL(sql, guestId, xBandId);
		
		if (retVal == true)
		{
			System.out.println("XBand removed");
			retVal = XBandController.setXBandActiveState(xBandId, false);
			System.out.println("XBand " + xBandId + " Deactivated");
		}
		
		return retVal;
	}
	
	public static Guest CreateDemoGuest(String guestName, String xBandId, String phone) throws Exception
	{
		
		String[] name = guestName.split(" ");
		
		
		
		boolean result = false;
		
		Guest g = new Guest();
		
		g.setFirstName(name[0]);
		
		if (name.length > 1)
		{
			g.setLastName(name[1]);
		}
		else
		{
			g.setLastName(" ");
		}
		
		g.setCreatedBy(Utils.getUserName());
		g.setActive(true);
		g.setState("WA");
		g.setXBMSId("");
		
		g = CreateGuest(g);
		
		if (g != null)
		{
			// Create a guestInfo object.
			GuestInfo gi = new GuestInfo();
			gi.setGuestId(BigInteger.valueOf(Long.parseLong(g.getGuestId())));
			gi.setCreatedBy(Utils.getUserName());
			gi.setGuest_InfotypeId(2);
			gi.setActive(true);
			gi.setGuest_Info(phone);

			
			GuestInfoController.SetGuestInfo(gi);
			
			result = AddXBandToGuest(g.getGuestId(), xBandId);
			
			if (!result)
			{
				g = null;
			}
		}

		return g;
	}

}
