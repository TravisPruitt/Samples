using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.Links;

namespace Disney.xBand.IDMS.Parties
{
    [DataContract]
    public class PartyMember
    {
        [DataMember(Name = "guestId")]
        public long GuestID { get; set; }

        [DataMember(Name = "isPrimary")]
        public bool IsPrimary { get; set; }

        [DataMember(Name = "links")]
        private LinkCollection Links { get; set; }
    }
}
