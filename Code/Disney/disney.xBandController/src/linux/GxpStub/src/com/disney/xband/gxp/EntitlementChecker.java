package com.disney.xband.gxp;

public class EntitlementChecker
{
	// single instance pattern
	public static EntitlementChecker INSTANCE = new EntitlementChecker();
	
	private EntitlementChecker()
	{
	}
	
	public TapResponse checkEntitlement(TapRequest treq)
	{
		TapResponse tres = new TapResponse();
		
		// TODO: Consult DB or whatever to determine what type of response to return.
		boolean bEntitlementOk = true;
		
		if (bEntitlementOk)
			tres.setGreen(true);
		else
			tres.setGreen(false);
		
		tres.setReason("");
		
		// for testing purposes, allow variable response
		if ((treq.getLocation() & 1)==0)					// even locations fail
		{
			tres.setGreen(false);
			tres.setReason("Unit test synthetic return value");
		}
		
		return tres;
	}
	
	public void createOverride(TapRequest treq)
	{
		// create an entitlement for the guest identified in the request
		// if the incoming station type is of type "combo", then redeem it
	}
	
	
	public void nextGuest(TapRequest treq)
	{
	}

}
