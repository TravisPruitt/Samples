package com.disney.xband.jms.lib.junit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.disney.xband.jms.lib.entity.assembly.GuestProfile;
import com.disney.xband.jms.lib.entity.common.GuestIdentifier;
import com.disney.xband.jms.lib.entity.xbms.XbandRequest;

public class AssemblyGuestProfileTest
{
	private StringBuilder json;

	@Before
	public void setUp() throws Exception
	{
		json = new StringBuilder();
		json.append("{");
		json.append("    \"title\": \"Miss\",");
		json.append("    \"firstName\": \"Mary\",");
		json.append("    \"lastName\": \"Davis\",");
		json.append("    \"dateOfBirth\": \"1990-02-03\",");
		json.append("    \"links\": {");
		json.append("        \"registeredProfile\": {");
		json.append("            \"href\": \"/assembly/guest/AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7/online-profile\"");
		json.append("        },");
		json.append("       \"ownedGuestsProfile\": {");
		json.append("            \"href\": \"/assembly/guest/AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7/managed-guest-profile-preferences\"");
		json.append("       },");
		json.append("        \"self\": {");
		json.append("            \"href\": \"/assembly/guest/AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7/profile\"");
		json.append("        },");
		json.append("        \"guestIdentifiers\": {");
		json.append("            \"href\": \"/assembly/guest/AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7/identifiers\"");
		json.append("        },");
		json.append("        \"guestAffiliations\": {");
		json.append("            \"href\": \"/assembly/guestAffiliations?guest-id-type=xid&guest-id-value=AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7\"");
		json.append("        }");
		json.append("    },");
		json.append("    \"guestType\": \"registered\",");
		json.append("    \"transactionalGuestProfileList\": [");
		json.append("        {");
		json.append("		\"links\": {");
		json.append("                \"self\": {");
		json.append("                    \"href\": \"/assembly/transactional-guest/F222657C270A45A09BD6AB879C9B3E90\"");
		json.append("                }");
		json.append("            }");
		json.append("        },");
		json.append("        {");
		json.append("            \"links\": {");
		json.append("                \"self\": {");
		json.append("                    \"href\": \"/assembly/transactional-guest/1EC5AD57B50343DF81C906526B7F023E\"");
		json.append("                }");
		json.append("            }");
		json.append("        }");
		json.append("    ],");
		json.append("    \"guestIdentifiers\": [");
		json.append("        {");
		json.append("            \"type\": \"transactional-guest-id\",");
		json.append("            \"value\": \"99028436\"");
		json.append("        },");
		json.append("        {");
		json.append("            \"type\": \"transactional-guest-id\",");
		json.append("            \"value\": \"99028431\"");
		json.append("        },");
		json.append("        {");
		json.append("            \"type\": \"gxp-link-id\",");
		json.append("            \"value\": \"555012\"");
		json.append("        },");
		json.append("        {");
		json.append("            \"type\": \"xbms-link-id\",");
		json.append("            \"value\": \"581C0671-03EF-49C0-9FEE-F7EEFEFEC938\"");
		json.append("        },");
		json.append("        {");
		json.append("            \"type\": \"xbms-link-id\",");
		json.append("            \"value\": \"BBE1CE89-0923-49B3-AD11-8DC30346C357\"");
		json.append("        },");
		json.append("        {");
		json.append("            \"type\": \"swid\",");
		json.append("            \"value\": \"{FA2CD801-5EA8-4675-A547-36F0D939FAD4}\"");
		json.append("        },");
		json.append("        {");
		json.append("            \"type\": \"xid\",");
		json.append("            \"value\": \"AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7\"");
		json.append("        }");
		json.append("    ],");
		json.append("    \"guestEligibility\": {");
		json.append("        \"magicPlusParticipantStatus\": \"ACTIVE\",");
		json.append("        \"magicPlusParticipantStatusEffectiveDate\": \"2013-03-05\",");
		json.append("        \"links\": {");
		json.append("            \"magicPlusParticipantStatusEvaluationRecords\": {");
		json.append("                \"href\": \"/assembly/guest/AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7/magic-plus-participant-status-evaluation-records\"");
		json.append("            }");
		json.append("        }");
		json.append("    }");
		json.append("}");
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
		
		try
		{
			GuestProfile guestProfile = mapper.readValue(json.toString(),
					GuestProfile.class);
			
			if(guestProfile.getTitle().compareToIgnoreCase("Miss") != 0)
			{
				fail("Incorrect Title");
			}
			
			if(guestProfile.getFirstName().compareToIgnoreCase("Mary") != 0)
			{
				fail("Incorrect First Name");
			}
			
			if(guestProfile.getLastName().compareToIgnoreCase("Davis") != 0)
			{
				fail("Incorrect Last Name");
			}
			
			int transactionalGuestIdCount = 0;
			boolean gxpLinkIdFound = false;
			int xbmsLinkIdCount = 0;
			boolean swidFound = false;
			boolean xidFound = false;
			
			if(guestProfile.getGuestIdentifiers().size() != 7)
			{
				fail("Invalid number of guest identifiers");
			}
			
			for(GuestIdentifier guestIdentifier : guestProfile.getGuestIdentifiers())
			{
				if(guestIdentifier.getType().compareToIgnoreCase("transactional-guest-id") == 0)
				{
					transactionalGuestIdCount++;
					if(guestIdentifier.getValue().compareTo("99028436") != 0 &&
					   guestIdentifier.getValue().compareTo("99028431") != 0)
					{
						fail("Invalid transactional-guest-id: " + guestIdentifier.getValue());
					}
							
				}
				
				if(guestIdentifier.getType().compareToIgnoreCase("gxp-link-id") == 0)
				{
					gxpLinkIdFound = true;
					if(guestIdentifier.getValue().compareTo("555012") != 0)
					{
						fail("Invalid gxp-link-id: " + guestIdentifier.getValue());
					}
				}
				
				if(guestIdentifier.getType().compareToIgnoreCase("xbms-link-id") == 0)
				{
					xbmsLinkIdCount++;
					if(guestIdentifier.getValue().compareTo("581C0671-03EF-49C0-9FEE-F7EEFEFEC938") != 0 &&
					   guestIdentifier.getValue().compareTo("BBE1CE89-0923-49B3-AD11-8DC30346C357") != 0)
					{
						fail("Invalid xbms-link-id: " + guestIdentifier.getValue());
					}
				}

				if(guestIdentifier.getType().compareToIgnoreCase("swid") == 0)
				{
					swidFound = true;
					if(guestIdentifier.getValue().compareTo("{FA2CD801-5EA8-4675-A547-36F0D939FAD4}") != 0)
					{
						fail("Invalid swid: " + guestIdentifier.getValue());
					}
				}
				
				if(guestIdentifier.getType().compareToIgnoreCase("xid") == 0)
				{
					xidFound = true;
					if(guestIdentifier.getValue().compareTo("AF55DFB4-CFBE-4C10-AD95-E0E3B0C5EEB7") != 0)
					{
						fail("Invalid xid: " + guestIdentifier.getValue());
					}
				}
			}
			
			if(transactionalGuestIdCount != 2)
			{
				fail("transactional-guest-id count: " + String.valueOf(transactionalGuestIdCount) + " incorrect.");
			}
			
			if(!gxpLinkIdFound)
			{
				fail("gxp-link-id not found.");
			}

			if(xbmsLinkIdCount != 2)
			{
				fail("xbms-link-id count: " + String.valueOf(xbmsLinkIdCount) + " incorrect.");
			}

			if(!swidFound)
			{
				fail("swid not found.");
			}

			if(!xidFound)
			{
				fail("xid not found.");
			}

		}
		catch (JsonParseException e)
		{
			fail(e.getLocalizedMessage());
		}
		catch (JsonMappingException e)
		{
			fail(e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			fail(e.getLocalizedMessage());
		}
		catch (Exception e)
		{
			fail(e.getLocalizedMessage());
		}
	}

}
