package com.disney.xband.jms.lib.entity.xbms;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.jms.lib.entity.common.GuestIdentifier;

/* 
{
"timestamp":"2012-08-07T00:42:36Z",
"type":"association",
"changeNotificationTrigger":"Accommodation",
"changeNotificationTriggerId":"356711353",
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
}
  */

@XmlRootElement
public class BandAssociationNotification 
{
    private String timestamp;
    private String type;
    private String changeNotificationTrigger;
    private String changeNotificationTriggerId;
    private Owner owner;
	private List<GuestIdentifier> resultingGuestIdentifiers;


	@XmlElement(name="timestamp")
	public String getTimestamp()
	{
		return this.timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	@XmlElement(name="type")
	public String getType()
	{
		return this.type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	@XmlElement(name="changeNotificationTrigger")
	public String getChangeNotificationTrigger()
	{
		return this.changeNotificationTrigger;
	}

	public void setChangeNotificationTrigger(String changeNotificationTrigger)
	{
		this.changeNotificationTrigger = changeNotificationTrigger;
	}

	@XmlElement(name="changeNotificationTriggerId")
	public String getChangeNotificationTriggerId()
	{
		return this.changeNotificationTriggerId;
	}

	public void setChangeNotificationTriggerId(String changeNotificationTriggerId)
	{
		this.changeNotificationTriggerId = changeNotificationTriggerId;
	}

	@XmlElement(name="owner")
	public Owner getOwner()
	{
		return this.owner;
	}

	public void setOwner(Owner owner)
	{
		this.owner = owner;
	}

	@XmlElement(name="resultingGuestIdentifiers")
	public List<GuestIdentifier> getResultingGuestIdentifiers()
	{
		return this.resultingGuestIdentifiers;
	}
	
	public void setResultingGuestIdentifiers(List<GuestIdentifier> resultingGuestIdentifiers)
	{
		this.resultingGuestIdentifiers = resultingGuestIdentifiers;
	}
}
