package com.disney.xband.jms.lib.entity.xbms;

import java.util.List;
/* 
 {
  "customizationSelections" : [
      {
        "xbandOwnerId" : "0E164AC1-6230-4DC5-A5D5-C5F30F60C43F",
        "self" : "/customization-selections/0E164AC1-6230-4DC5-A5D5-C5F30F60C43F",
        "xband" : "/xband/0E164AC1-6230-4DC5-A5D5-C5F30F60C43F",
        "birthDate" : "1975-02-01T05:00:00Z",
        "firstName" : "Jane",
        "guestId" : 10000,
        "guestIdType" : "transactional-guest-id",
        "primaryGuest" : true,
        "lastName" : "Doe",
        "bandProductCode" : "B11111",
        "bandAccessories" : [
          {
            "customizationSelections": "100000",
            "bandAccessoryType": "100000"
          }
        ],
        "printedName" : "Jane's Band",
        "customizationSelectionId" : "0E164AC1-6230-4DC5-A5D5-C5F30F60C43F",
        "createDate" : "2010-07-26T05:00:00Z",
        "updateDate" : "2010-07-26T05:00:00Z",
        "entitlements" : [ "STANDARD", "LEVEL_2_5" ],
        "qualifyingIds" : [
          {
            "qualifyingId": 1234579,
            "qualifyingIdType": "travel-component-id"
          },
          {
            "qualifyingId": 1234578,
            "qualifyingIdType": "travel-component-id"
          },
          {
            "qualifyingId": 4387621045,
            "qualifyingIdType": "bundle-id"
          },
          {
            "qualifyingId": 4387621048,
            "qualifyingIdType": "bundle-id"
          },
        ],
        "confirmedCustomization" : false,
      },
      {
        "xbandOwnerId" : "D7C0EF05-5078-4ECD-A3DF-D53A65E0A75C",
        "self" : "/customization-selections/8D60B701-22EA-487D-8950-D185B46D4EDC",
        "xband" : "/xband/8D60B701-22EA-487D-8950-D185B46D4EDC",
        "birthDate" : "1980-01-01T05:00:00Z",
        "firstName" : "Joey",
        "guestId" : 10001,
        "guestIdType" : "transactional-guest-id",
        "primaryGuest" : false,
        "lastName" : "Tsai",
        "bandProductCode" : "B11111",
        "bandAccessories" : [
          {
            "customizationSelections": "100000",
            "bandAccessoryType": "100000"
          }
        ],
        "printedName" : "Joey's band",
        "customizationSelectionId" : "8D60B701-22EA-487D-8950-D185B46D4EDC",
        "createDate" : "2010-07-26T05:00:00Z",
        "updateDate" : "2010-07-26T05:00:00Z"
        "entitlements" : [ "NONE" ],
        "qualifyingIds" : [
          {
            "qualifyingId": 1234578,
            "qualifyingIdType": "travel-component-id"
          }
        ],
        "confirmedCustomization" : false,
      },
    ],
  "requestAddress" :
    {
      "confirmedAddress" : false,
      "phoneNumber" : "867-5309",
      "address" : {
          "address1" : "#4 Privet Drive",
          "city" : "Atlanta",
          "country" : "US",
          "postalCode" : "30328",
          "state" : "GA"
      }
    },
  "resortReservations": [
    {
      "arrivalDate" : "2010-09-26T04:00:00Z",
      "departureDate" : "2010-10-09T04:00:00Z",
      "facilityId" : 280010388,
      "travelSegmentId": 1234578,
      "travelComponentId": 87654321
    },
    {
      "arrivalDate" : "2010-09-26T04:00:00Z",
      "departureDate" : "2010-10-09T04:00:00Z",
      "facilityId" : 280010400,
      "travelSegmentId": 1234579,
      "travelComponentId": 87654320
    }
  ],
  "shipment" : {
      "method": "PRIMARY_GUEST_ADDRESS_BEST",
      "carrier" : "UPS",
      "carrierLink" : "http://www.ups.com/WebTracking/track",
      "shippingDate" : "2010-08-27T04:00:00Z",
      "trackingNumber" : 66,
      "address" : {
          "address1" : "#4 Privet Drive",
          "city" : "Atlanta",
          "country" : "US",
          "postalCode" : 30328,
          "state" : "GA"
      }
  },

  "state" : "COMPLETED",
  "createDate" : "2010-07-26T05:00:00Z",
  "updateDate" : "2010-07-26T05:00:00Z",
  "customizationEndDate" : "2010-08-26T05:00:00Z",
  "xbandRequestId" : "B2512223-7314-46EE-9687-8B6159ECAD08",
  "primaryGuestOwnerId":"EAC037D6-3B51-4EB4-92D6-0DC23F487C08"
  "self" : "/xband-requests/B2512223-7314-46EE-9687-8B6159ECAD08",
  "reorder" : "/xband-requests/B2512223-7314-46EE-9687-8B6159ECAD08/reorder",
  "options": "/reorder-options/458414CE-602C-4ECC-B01D-81D4BE7EC29C",
  "order": "/orders/85E5B54C-3727-4A57-8B13-5570D4B3657D",
  "acquisitionId" : "100077",
  "acquisitionIdType" : "travel-plan-id",
  "acquisitionStartDate" : "2010-08-26T05:00:00Z",
  "acquisitionUpdateDate" : "2012-01-26T05:00:00.568Z",
}


 */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XbandRequest 
{
	private List<CustomizationSelection> customizationSelections;
	private RequestAddress requestAddress;
	private List<ResortReservation> resortReservations;
	private Shipment shipment;
	private String state;
	private String createDate;
	private String updateDate;
	private String customizationEndDate;
	private String xbandRequestId;
	private String self;
	private String reorder;
	private String options;
	private String order;
	private String acquisitionId;
	private String acquisitionIdType;
	private String acquisitionStartDate;
	private String acquisitionUpdateDate;
	private String primaryGuestOwnerId;
	
	@XmlElement(name="customizationSelections")
	public List<CustomizationSelection> getCustomizationSelections()
	{
		return this.customizationSelections;
	}

	public void setCustomizationSelections(List<CustomizationSelection> customizationSelections)
	{
		this.customizationSelections = customizationSelections;
	}

	@XmlElement(name="requestAddress")
	public RequestAddress getRequestAddress()
	{
		return this.requestAddress;
	}

	public void setRequestAddress(RequestAddress requestAddress)
	{
		this.requestAddress = requestAddress;
	}
	
	@XmlElement(name="resortReservations")
	public List<ResortReservation> getResortReservations()
	{
		return this.resortReservations;
	}

	public void setResortReservations(List<ResortReservation> resortReservations)
	{
		this.resortReservations = resortReservations;
	}
	
	@XmlElement(name="shipment")
	public Shipment getShipment()
	{
		return this.shipment;
	}

	public void setShipment(Shipment shipment)
	{
		this.shipment = shipment;
	}
	
	@XmlElement(name="state")
	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
	}
	
	@XmlElement(name="createDate")
	public String getCreateDate()
	{
		return this.createDate;
	}

	public void setCreateDate(String createDate)
	{
		this.createDate = createDate;
	}
	
	@XmlElement(name="updateDate")
	public String getUpdateDate()
	{
		return this.updateDate;
	}

	public void setUpdateDate(String updateDate)
	{
		this.updateDate = updateDate;
	}
	
	@XmlElement(name="customizationEndDate")
	public String getCustomizationEndDate()
	{
		return this.customizationEndDate;
	}

	public void setCustomizationEndDate(String customizationEndDate)
	{
		this.customizationEndDate = customizationEndDate;
	}
	
	@XmlElement(name="xbandRequestId")
	public String getXbandRequestId()
	{
		return this.xbandRequestId;
	}

	public void setXbandRequestId(String xbandRequestId)
	{
		this.xbandRequestId = xbandRequestId;
	}
	
	@XmlElement(name="self")
	public String getSelf()
	{
		return this.self;
	}

	public void setSelf(String self)
	{
		this.self = self;
	}
	
	@XmlElement(name="reorder")
	public String getReorder()
	{
		return this.reorder;
	}

	public void setReorder(String reorder)
	{
		this.reorder = reorder;
	}
	
	@XmlElement(name="options")
	public String getOptions()
	{
		return this.options;
	}

	public void setOptions(String options)
	{
		this.options = options;
	}
	
	@XmlElement(name="order")
	public String getOrder()
	{
		return this.order;
	}

	public void setOrder(String order)
	{
		this.order = order;
	}
	@XmlElement(name="acquisitionId")
	public String getAcquisitionId()
	{
		return this.acquisitionId;
	}

	public void setAcquisitionId(String acquisitionId)
	{
		this.acquisitionId = acquisitionId;
	}
	
	@XmlElement(name="acquisitionIdType")
	public String getAcquisitionIdType()
	{
		return this.acquisitionIdType;
	}

	public void setAcquisitionIdType(String acquisitionIdType)
	{
		this.acquisitionIdType = acquisitionIdType;
	}
	
	@XmlElement(name="acquisitionStartDate")
	public String getAcquisitionStartDate()
	{
		return this.acquisitionStartDate;
	}

	public void setAcquisitionStartDate(String acquisitionStartDate)
	{
		this.acquisitionStartDate = acquisitionStartDate;
	}
	
	@XmlElement(name="acquisitionUpdateDate")
	public String getAcquisitionUpdateDate()
	{
		return this.acquisitionUpdateDate;
	}

	public void setAcquisitionUpdateDate(String acquisitionUpdateDate)
	{
		this.acquisitionUpdateDate = acquisitionUpdateDate;
	}
	
	@XmlElement(name="primaryGuestOwnerId")
	public String getPrimaryGuestOwnerId()
	{
		return this.primaryGuestOwnerId;
	}

	public void setPrimaryGuestOwnerId(String primaryGuestOwnerId)
	{
		this.primaryGuestOwnerId = primaryGuestOwnerId;
	}
}
