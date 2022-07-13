using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.Links;

namespace Disney.xBand.IDMS.Guests
{
    [DataContract]
    public class Guest
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

        //[DataMember(Name = "type")]
        //public string IDMSTypeName { get; set; }

        [DataMember(Name = "partyId")]
        public long partyID { get; set; }

        public Guest()
        {
        }
    }
}
