using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.GuestIdentifiers
{
    [DataContract]
    public class GuestIdentifier
    {
        [DataMember(Name="value")]
        public string Value { get; set; }

        [DataMember(Name = "type")]
        public string TypeName { get; set; }

        [DataMember(Name = "guestId")]
        public long GuestID { get; set; }
    }
}
