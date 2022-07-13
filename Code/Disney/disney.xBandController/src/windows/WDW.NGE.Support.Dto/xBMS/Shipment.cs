using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class Shipment
    {
        [DataMember(Name = "address", Order=1)]
        public Address Address { get; set; }

        [DataMember(Name = "method", Order=2)]
        public String Method { get; set; }

        [DataMember(Name = "carrier", Order=3)]
        public String Carrier { get; set; }

        [DataMember(Name = "carrierLink", Order=4)]
        public String CarrierLink { get; set; }

        [DataMember(Name = "trackingNumber", Order=5)]
        public String TrackingNumber { get; set; }

        [DataMember(Name = "shippingDate", Order=6)]
        public String ShippingDate { get; set; }
    }
}
