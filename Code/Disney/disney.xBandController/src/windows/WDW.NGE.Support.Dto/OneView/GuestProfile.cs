using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.OneView
{
    [DataContract]
    public class GuestProfile : Common.IGuestProfile
    {
        [DataMember(Name = "title",Order=1)]
        public string Title { get; set; }

        [DataMember(Name = "lastName", Order = 2)]
        public String LastName { get; set; }

        [DataMember(Name = "firstName", Order = 3)]
        public String FirstName { get; set; }

        [DataMember(Name = "age", Order = 4)]
        public String Age { get; set; }

        [DataMember(Name = "dateOfBirth", Order = 5)]
        public String DateOfBirth { get; set; }

        [DataMember(Name = "guestIdentifiers", Order = 6)]
        public List<Common.GuestIdentifier> GuestIdentifiers { get; set; }

        [DataMember(Name = "guestEligibility", Order = 7)]
        public GuestEligibility GuestEligibility { get; set; }
    }
}
