using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [DataContract]
    public class CustomizationSelection
    {
        [DataMember(Name = "guestId", Order = 1)]
        public String GuestId { get; set; }

        [DataMember(Name = "guestIdType", Order = 2)]
        public String GuestTypeId { get; set; }

        [DataMember(Name = "bandAccessories", Order = 3)]
        public List<BandAccessory> BandAccessories { get; set; }

        [DataMember(Name = "entitlements", Order = 4)]
        public List<String> Entitlements { get; set; }

        [DataMember(Name = "customizationSelectionId", Order = 5)]
        public String CustomizationSelectionId { get; set; }

        [DataMember(Name="qualifyingIds", Order=6)]
        public List<QualifyingId> QualifyingIds { get; set; }

        [DataMember(Name="bandProductCode", Order=7)]
        public String BandProductCode { get; set; }

        [DataMember(Name="printedName", Order=8)]
        public String PrintedName { get; set; }

        [DataMember(Name="firstName", Order=9)]
        public String FirstName { get; set; }

        [DataMember(Name="primaryGuest", Order=10)]
        public Boolean PrimaryGuest { get; set; }

        [DataMember(Name="birthDate", Order=11)]
        public String BirthDate { get; set; }

        [DataMember(Name="lastName", Order=12)]
        public String LastName { get; set; }

        [DataMember(Name="xbandRequestId", Order=13)]
        public String xBandRequestId { get; set; }

        [DataMember(Name="xbandOwnerId", Order=14)]
        public String xBandOwnerId { get; set; }

        [DataMember(Name="createDate", Order=15)]
        public String CreateDate { get; set; }

        [DataMember(Name="updateDate", Order=16)]
        public String UpdateDate { get; set; }

        [DataMember(Name="self", Order=17)]
        public String Self { get; set; }
    }
}
