using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.OneView
{
    [DataContract]
    public class Entitlement
    {
//"startTime": "2013-05-30T04:00:00Z",
//"endTime": "2013-05-31T04:00:00Z",
//"type": "resort-reservation",
//"id": "431421413293",    

        [DataMember(Name = "startTime", Order = 1)]
        public String StartTime { get; set; }

        [DataMember(Name = "endTime", Order = 2)]
        public String EndTime { get; set; }

        [DataMember(Name = "type", Order = 3)]
        public String Type { get; set; }

        [DataMember(Name = "id", Order = 4)]
        public String ID { get; set; }

        [DataMember(Name = "links", Order = 5)]
        public EntitlementLinks Links { get; set; }

        [DataMember(Name = "guests", Order = 6)]
        public List<EntitlementGuest> Guests { get; set; }

    }
}
