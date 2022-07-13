using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class CustomizationSelection
    {
        [DataMember(Name = "xbandOwnerId", Order=1)]
        public String XbandOwnerId { get; set; }

        [DataMember(Name = "xbandRequestId", Order=2)]
        public String XbandRequestId { get; set; }

        [DataMember(Name = "guestId", Order=3)]
        public String GuestId { get; set; }

        [DataMember(Name = "guestIdType", Order=4)]
        public String GuestIdType { get; set; }

        //Ingnoring "bandAccessories": [ ],

        [DataMember(Name = "entitlements", Order=6)]
        public List<String> Entitlements { get; set; }

        [DataMember(Name = "customizationSelectionId", Order=7)]
        public String CustomizationSelectionId { get; set; }

        [DataMember(Name = "qualifyingIds", Order=8)]
        public List<QualifyingId> QualifyingIds { get; set; }

        [DataMember(Name = "bandProductCode", Order=9)]
        public String BandProductCode { get; set; }

        [DataMember(Name = "printedName", Order=10)]
        public String PrintedName { get; set; }

        [DataMember(Name = "confirmedCustomization", Order=11)]
        public Boolean ConfirmedCustomization { get; set; }

        [DataMember(Name = "firstName", Order=12)]
        public String FirstName { get; set; }

        [DataMember(Name = "primaryGuest", Order=13)]
        public Boolean PrimaryGuest { get; set; }

        [DataMember(Name = "createDate", Order=14)]
        public String CreateDate { get; set; }

        [DataMember(Name = "lastName", Order=15)]
        public String LastName { get; set; }

        [DataMember(Name = "updateDate", Order=16)]
        public String UpdateDate { get; set; }

        [DataMember(Name = "xband", Order=17)]
        public String Xband { get; set; }

        [DataMember(Name = "self", Order=18)]
        public String Self { get; set; }
    }
}
