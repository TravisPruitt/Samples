using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.Links;

namespace Disney.xBand.IDMS.Parties
{
    [DataContract]
    public class Party
    {
        [DataMember(Name = "partyId")]
        public long PartyID { get; set; }

        [DataMember(Name = "primaryGuestId")]
        public long PrimaryGuestID { get; set; }

        [DataMember(Name = "partyName")]
        public String PartyName { get; set; }

        [DataMember(Name = "count")]
        public int Count { get; set; }

        [DataMember(Name = "members")]
        private List<PartyMember> Members { get; set; }

        [DataMember(Name = "links")]
        private LinkCollection Links { get; set; }
    }
}
