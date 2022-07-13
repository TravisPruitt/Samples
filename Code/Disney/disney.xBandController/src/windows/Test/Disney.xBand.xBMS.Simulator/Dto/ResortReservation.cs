using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [DataContract]
    public class ResortReservation
    {
        [DataMember(Name="travelSegmentId", Order=1)]
        public long TravelSegmentId { get; set; }

        [DataMember(Name="travelComponentId", Order=2)]
        public long TravelComponentId { get; set; }

        [DataMember(Name="arrivalDate", Order=3)]
        public String ArrivalDate { get; set; }

        [DataMember(Name="facilityId", Order=3)]
        public long FacilityId { get; set; }

        [DataMember(Name="departureDate", Order=4)]
        public String DepartureDate { get; set; }
    }
}
