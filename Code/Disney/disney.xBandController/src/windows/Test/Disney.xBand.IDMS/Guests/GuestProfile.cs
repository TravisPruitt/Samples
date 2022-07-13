using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.GuestIdentifiers;
using Disney.xBand.IDMS.xBand;

namespace Disney.xBand.IDMS.Guests
{
    [DataContract]
    public class GuestProfile
    {
        [DataMember(Name = "guestId")]
        public long GuestID { get; set; }

        [DataMember(Name = "dateOfBirth")]
        public String DateOfBirth { get; set; }

        [DataMember(Name = "status")]
        public string Status { get; set; }

        [DataMember(Name = "countryCode")]
        public string CountryCode { get; set; }

        [DataMember(Name = "swid")]
        public string SWID { get; set; }

        [DataMember(Name = "languageCode")]
        public string LanguageCode { get; set; }

        [DataMember(Name = "emailAddress")]
        public string EmailAddress { get; set; }

        [DataMember(Name = "parentEmail")]
        public string ParentEmail { get; set; }

        [DataMember(Name = "name")]
        public GuestName Name { get; set; }

        [DataMember(Name = "gender")]
        public string Gender { get; set; }

        [DataMember(Name = "userName")]
        public string UserName { get; set; }

        [DataMember(Name = "avatar")]
        public string Avatar { get; set; }

        [DataMember(Name = "visitCount")]
        public int VisitCount { get; set; }

        [DataMember(Name = "idmstypeid")]
        public int IDMSTypeID { get; set; }

        [DataMember(Name = "type")]
        public string IdmsType { get; set; }

        [DataMember(Name = "partyId")]
        public long partyID { get; set; }

        [DataMember(Name = "identifiers")]
        public List<GuestIdentifier> GuestIdentifiers { get; set; }
        
        [DataMember(Name = "xbands")]
        public List<Xband> xBands { get; set; }
    }
}
