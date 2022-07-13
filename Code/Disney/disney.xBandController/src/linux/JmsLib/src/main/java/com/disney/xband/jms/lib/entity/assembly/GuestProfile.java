package com.disney.xband.jms.lib.entity.assembly;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.disney.xband.jms.lib.entity.common.GuestIdentifier;

/*
{

    "title": "Mr.",com.disney.xband.jms.lib.entity.common
    "suffix": "JR",
    "firstName": "Walter",
    "middleName": "F",
    "lastName": "Fennell",
    "dateOfBirth": "1949-06-29",
    "avatar": {
        "links": {
            "self": {
                "href": "http://nge-prod-zeus.wdw.disney.com:8080/cm/services/guest/v1/character/15655408"
            }
        },
        "name": "Default Avatar"
    },
    "favoriteCharacter": {
        "links": {
            "self": {
                "href": "http://nge-prod-zeus.wdw.disney.com:8080/cm/services/guest/v1/character/15655408"
            }com.disney.xband.jms.lib.entity.common
        },
        "name": "Default Avatar"
    },
    "links": {
        "registeredProfile": {
            "href": "/assembly/guest/515D322C-175D-489D-B25F-09C3FCE05E30/online-profile"
        },
        "ownedGuestsProfile": {
            "href": "/assembly/guest/515D322C-175D-489D-B25F-09C3FCE05E30/managed-guest-profile-preferences"
        },
        "wdproAvatar": {
            "href": "https://api.wdpro.disney.go.com/facility-service/avatars/15655408"
        },
        "wdproFavoriteCharacter": {
            "href": "https://api.wdpro.disney.go.com/facility-service/characters/15655408"
        },
        "self": {
            "href": "/assembly/guest/515D322C-175D-489D-B25F-09C3FCE05E30/profile"
        },
        "guestIdentifiers": {
            "href": "/assembly/guest/515D322C-175D-489D-B25F-09C3FCE05E30/identifiers"
        }
    },
    "guestType": "registered",
    "transactionalGuestProfileList": [
        {com.disney.xband.jms.lib.entity.common
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/fd505da3164b7aaa673c2f0929ae7b40"
                }
            }
        },
        {
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/2ef3146c4ca662dcdd61afea246bdac3"
                }
            }
        },
        {com.disney.xband.jms.lib.entity.common
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/5cb631d6d51658df7da5ea27c398ba08"
                }
            }
        },
        {
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/64dd163a14cf6c885aaf673f1f3bbf5b"
                }com.disney.xband.jms.lib.entity.common
            }
        },
        {
            "links": {com.disney.xband.jms.lib.entity.common
                "self": {
                    "href": "/assembly/transactional-guest/131bf47228c9a57148e1d0d3e6d549c8"
                }
            }
        },
        {
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/0bc486077fc3173608845914455eb370"
                }
            }
        },
        {
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/d63c04f31f2317f118078df0fd886f09"
                }
            }
        },
        {
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/111f90787d717217e222154349f76eeb"
                }
            }
        },
        {
            "links": {
                "self": {
                    "href": "/assembly/transactional-guest/369348b326f1b22786bb75925e591215"
                }
            }
        }
    ],
    "DMEGuestList": [
        {
            "links": {
                "self": {
                    "href": "/assembly/dme-guest/8c1d4b66a605dc16347941828dd72c74"
                }
            }
        }
    ],
    "guestIdentifiers": [
        {
            "type": "dme-link-id",
            "value": "32356690"
        },
        {
            "type": "transactional-guest-id",
            "value": "56257977"
        },
        {
            "type": "transactional-guest-id",
            "value": "56253931"
        },
        {
            "type": "transactional-guest-id",
            "value": "56245951"
        },
        {
            "type": "transactional-guest-id",
            "value": "50413879"
        },
        {
            "type": "transactional-guest-id",
            "value": "49905814"
        },
        {
            "type": "transactional-guest-id",
            "value": "48352375"
        },
        {
            "type": "transactional-guest-id",
            "value": "45424128"
        },
        {
            "type": "transactional-guest-id",
            "value": "40634330"
        },
        {
            "type": "transactional-guest-id",
            "value": "31035755"
        },
        {
            "type": "xband-longrange-public-id",
            "value": "8824959"
        },
        {
            "type": "xband-longrange-public-id",
            "value": "8519526"
        },
        {
            "type": "payment-link-id",
            "value": "206415"
        },
        {
            "type": "xband-secure-id",
            "value": "1009253640729573"
        },
        {
            "type": "xband-secure-id",
            "value": "1008933370923402"
        },
        {
            "type": "xband-shortrange-public-id",
            "value": "8824959"
        },
        {
            "type": "xband-shortrange-public-id",
            "value": "8519526"
        },
        {
            "type": "xband-shortrange-tag",
            "value": "37203333169386244"
        },
        {
            "type": "xband-shortrange-tag",
            "value": "37202818847066884"
        },
        {
            "type": "xbandid",
            "value": "86F0DB21-5C5D-4BC0-8F0B-13A94624B80A"
        },
        {com.disney.xband.jms.lib.entity.common
            "type": "xbandid",
            "value": "39EA2760-D1AF-4D69-BC5F-DC9D9FFD415F"
        },
        {
            "type": "xband-external-number",
            "value": "306032037013"
        },
        {
            "type": "xband-external-number",
            "value": "304116117125"
        },
        {
            "type": "xbms-link-id",
            "value": "FCF910BD-50DA-4652-B725-3F6BEE39FAED"
        },
        {
            "type": "xbms-link-id",
            "value": "D907B6DB-427A-4AF8-B005-0BB4E7E947B6"
        },
        {
            "type": "swid",
            "value": "{502454DB-B959-4321-9441-8A422823B6EB}"
        },
        {
            "type": "xid",
            "value": "515D322C-175D-489D-B25F-09C3FCE05E30"
        }
    ],
    "guestEligibility": {
        "magicPlusParticipantStatus": "NONE"
    }

}
*/

@XmlRootElement
@XmlType(propOrder={"title","suffix","firstName","middleName","lastName","guestIdentifiers"})
public class GuestProfile
{
	private String title;
	private String suffix;
	private String firstName;
	private String lastName;
	private List<GuestIdentifier> guestIdentifiers;
	    
	@XmlElement(name="title")
	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@XmlElement(name="suffix")
	public String getSuffix()
	{
		return this.suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	@XmlElement(name="firstName")
	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@XmlElement(name="lastName")
	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
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

}
