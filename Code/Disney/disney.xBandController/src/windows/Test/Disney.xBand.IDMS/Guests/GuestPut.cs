using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.GuestIdentifiers;

namespace Disney.xBand.IDMS.Guests
{
    [DataContract]
    public class GuestPut
    {
        [DataMember(Name = "countryCode")]
        public string CountryCode { get; set; }

        [DataMember(Name = "languageCode")]
        public string LanguageCode { get; set; }

        [DataMember(Name = "emailAddress")]
        public string EmailAddress { get; set; }

        [DataMember(Name = "parentEmail")]
        public string ParentEmail { get; set; }

        [DataMember(Name = "name")]
        public GuestName Name { get; set; }

        [DataMember(Name = "partyId")]
        public long partyID { get; set; }
    }
}
