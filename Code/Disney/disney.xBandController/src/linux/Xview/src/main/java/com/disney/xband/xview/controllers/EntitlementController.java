package com.disney.xband.xview.controllers;

import java.util.ArrayList;
import java.util.List;

import com.disney.xband.xview.lib.model.*;


public class EntitlementController extends baseController implements
		IController 
{
	
	public static Entitlement GetEntitlementById(String id)
	{
		Entitlement entitlement = new Entitlement();
		
		return entitlement;
	}
	
	public static boolean SaveEntitlement(Entitlement entitlement)
	{
		return false;
	}
	
	public static Entitlement CreateEntitlement(Entitlement entitlement)
	{
		return entitlement;
	}
	
	public static boolean DeleteEntitlement(Entitlement entitlement)
	{
		return false;
	}
	
	public static List<Entitlement> GetAllEntitlements()
	{
		List<Entitlement> entitlement = new ArrayList<Entitlement>();
		
		return entitlement;
	}

}
