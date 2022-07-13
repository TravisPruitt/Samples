using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.Parties
{
    [DataContract]
    public class GuestPartyAssign
    {
        [DataMember(Name = "guestId")]
        public long GuestID { get; set; }

        [DataMember(Name = "partyId")]
        public long PartyID { get; set; }
    }
}
