package com.disney.xband.xview.controllers;

import java.util.*;

import com.disney.xband.xview.Utils;
import com.disney.xband.xview.lib.model.*;


public class GuestInfoController extends baseController implements IController {
	
	
	
	public static List<GuestInfo> GetGuestInfosForGuest(String guestId) throws Exception
	{
		List<GuestInfo> guestInfos = new ArrayList<GuestInfo>();
		
		String sql = "SELECT * FROM guest_info where guestId=?";
		
		GuestInfo guestInfo = new GuestInfo();
		
		List<baseModel> models = getResultSetAndClose(sql, guestInfo, guestId);
		
		if (!models.isEmpty())
		{
			for(baseModel model : models)
			{
				guestInfos.add((GuestInfo) model);
			}
		}
		
		return guestInfos;
	}
	
	public static boolean SetGuestInfo(GuestInfo gi) throws Exception
	{
		boolean result = false;
		
		String sql = "INSERT INTO guest_info" +
				"( guestId" +
				", guest_InfoTypeId" +
				", guest_Info" +
				", active" +
				", createdBy" +
				", createdDate) " +
				"VALUES " +
				"(?, ?, ?, ?, ?, ?)";
				
		result = ExecuteSQL(
                sql,
                gi.getGuestId().toString(), gi.getGuest_InfotypeId() + "", gi.getGuest_Info(),
                gi.getActive() + "", Utils.getUserName(), Utils.getDateTimeNow()
        );
		
		return result;
	}
	
	public static boolean UpdateGuestInfo(GuestInfo gi) throws Exception
	{
		boolean result = false;
		int active = 0;
		
		if (gi.getActive())
			active = 1;
		
				
		
		String sql = "UPDATE guest_info" +
				"SET " +
				"guestInfo_typeId=?" +
				", guestInfo=?" +
				", active=?" +
				", createdBy=?" +
				", createdDate=?" +
				"WHERE guestId=?";
		
		result = ExecuteSQL(
                sql,
                gi.getGuest_InfotypeId() + "", gi.getGuest_Info(), active + "",
                Utils.getUserName(), Utils.getDateTimeNow(), gi.getGuestId() + ""
        );
		
		return result;
	}
	
	public static boolean RemoveGuestInfo(String guestId, String guestInfoId) throws Exception
	{
		boolean result = false;
		
		String sql = "DELETE from guest_info where guestId=? AND guestInfoId=?";
		
		result = ExecuteSQL(sql, guestId, guestInfoId);
		
		return result;
	}
	
	
	

}
