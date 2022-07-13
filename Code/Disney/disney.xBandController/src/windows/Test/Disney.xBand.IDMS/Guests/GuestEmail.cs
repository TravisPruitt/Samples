using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.Guests
{
    [DataContract]
    public class GuestEmail
    {
        [DataMember(Name="guestId")]
        public long GuestID { get; set; }

        [DataMember(Name = "emailAddress")]
        public string EmailAddress { get; set; }

        [DataMember(Name = "visitCount")]
        public int VisitCount { get; set; }
    }
}
