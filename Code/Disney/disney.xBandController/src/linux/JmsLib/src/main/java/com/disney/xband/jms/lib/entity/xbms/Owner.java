package com.disney.xband.jms.lib.entity.xbms;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.jms.lib.entity.common.GuestIdentifier;

/* 
"owner":
	{
	"links":
		{
		"self":
			{
			"href":"/assembly/guest/guest/6A3707A6-97E1-480B-AF80-6B6CF2869528/profile"
			}
		},
	"guestIdentifiers":[
		{
		"type":"xid",
		"value":"6A3707A6-97E1-480B-AF80-6B6CF2869528"
		},
		{
		"type":"swid",
		"value":"{C7EC172C-0880-46EB-AC17-2C0880F6EB4C}"
		},
		{
		"type":"transactional-guest-id",
		"value":"77220345"
		},
		{
		"type":"gxp-link-id",
		"value":"535431"
		},
		{
		"type":"swid",
		"value":"{C7EC172C-0880-46EB-AC17-2C0880F6EB4C}"
		},
		{
		"type":"xpassid",
		"value":"18065038"
		},
		{
		"type":"xpassid",
		"value":"18065039"
		},
		{"type":"xpassid",
		"value":"18065040"
		}],
		"entitlements":
		{
		"xpasses":[
			{
			"id":"18065040",
			"startTime":"2012-08-07T00:29:34Z",
			"endTime":"2012-08-07T00:29:34Z",
			"links":
				{
				"self":
					{
					"href":"/assembly/xpass/e86d2cf8c0fe66dc59b03bcc783757b8"
					}
				}
			},
			{
			"id":"18065039",
			"startTime":"2012-08-07T00:29:34Z",
			"endTime":"2012-08-07T00:29:34Z",
			"links":
				{
				"self":
					{
						"href":"/assembly/xpass/8988875ac2fba8a8c2824c2bf31756e4"
					}
				}
			},
			{
			"id":"18065038",
			"startTime":"2012-08-07T00:29:34Z",
			"endTime":"2012-08-07T00:29:34Z",
			"links":
				{
				"self":
					{
					"href":"/assembly/xpass/955d53fce483b1fdca6e09d82b7a1ae8"
					}
				}
			}]
		},
		"xbands":
			{
			"self":
				{
				"href":"/xband-web/guest/6A3707A6-97E1-480B-AF80-6B6CF2869528/managed-xbands"
				}
			}
		}
 */

@XmlRootElement
@XmlType(propOrder={"links","guestIdentifiers","entitlements"})
public class Owner 	
{
	private LinkCollection links;
	private List<GuestIdentifier> guestIdentifiers;
	private Entitlements entitlements;
	private Xbands xbands;
	
	@XmlElement(name="links")
	public LinkCollection getLinks()
	{
		return this.links;
	}

	public void setLinks(LinkCollection links)
	{
		this.links = links;
	}
	
	@XmlElement(name="guestIdentifiers")
	public List<GuestIdentifier> getGuestIdentifiers()
	{
		return this.guestIdentifiers;
	}

	public void setGuestIdentifiers(List<GuestIdentifier> guestIdentifiers)
	{
		this.guestIdentifiers = guestIdentifiers;
	}

	@XmlElement(name="entitlements")
	public Entitlements getEntitlements()
	{
		return this.entitlements;
	}

	public void setEntitlements(Entitlements entitlements)
	{
		this.entitlements = entitlements;
	}

	@XmlElement(name="xbands")
	public Xbands getXbands()
	{
		return this.xbands;
	}

	public void setXbands(Xbands xbands)
	{
		this.xbands = xbands;
	}

}
