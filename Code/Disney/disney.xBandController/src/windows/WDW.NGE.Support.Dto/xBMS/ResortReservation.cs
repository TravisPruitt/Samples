using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class ResortReservation
    {
        [DataMember(Name="facilityId", Order=1)]
        public long facilityId { get; set; }

        [DataMember(Name="travelSegmentId", Order=2)]
        public long TravelSegmentId { get; set; }

        [DataMember(Name="travelComponentId", Order=3)]
        public long TravelComponentId { get; set; }

        [DataMember(Name="arrivalDate", Order=4)]
        public String ArrivalDate { get; set; }

        [DataMember(Name="departureDate", Order=5)]
        public String DepartureDate { get; set; }
    }
}
