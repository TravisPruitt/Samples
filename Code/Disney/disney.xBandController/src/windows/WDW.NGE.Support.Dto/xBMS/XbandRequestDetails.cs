using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class XbandRequestDetails
    {
        [DataMember(Name = "options", Order=1)]
        public String Options { get; set; }

        [DataMember(Name = "order", Order=2)]
        public String Order { get; set; }
        
        [DataMember(Name = "state", Order=3)]
        public String State { get; set; }

        [DataMember(Name = "primaryGuestOwnerId", Order=4)]
        public String PrimaryGuestOwnerId { get; set; }

        [DataMember(Name = "xbandRequestId", Order=5)]
        public String xbandRequestId { get; set; }

        [DataMember(Name = "acquisitionId", Order=6)]
        public String AcquisitionId { get; set; }

        [DataMember(Name = "acquisitionIdType", Order=7)]
        public String AcquisitionIdType { get; set; }

        [DataMember(Name = "acquisitionStartDate", Order=8)]
        public String AcquisitionStartDate { get; set; }

        [DataMember(Name = "resortReservations", Order = 9)]
        public List<ResortReservation> ResortReservations { get; set; }

        [DataMember(Name = "requestAddress", Order = 10)]
        public RequestAddress RequestAddress { get; set; }

        [DataMember(Name = "shipment", Order = 11)]
        public Shipment Shipment { get; set; }

        [DataMember(Name = "customizationSelections", Order = 12)]
        public List<CustomizationSelection> CustomizationSelections { get; set; }

        [DataMember(Name = "acquisitionUpdateDate", Order=13)]
        public String AcquisitionUpdateDate { get; set; }

        [DataMember(Name = "createDate", Order=14)]
        public String CreateDate { get; set; }

        [DataMember(Name = "updateDate", Order=15)]
        public String UpdateDate { get; set; }

        [DataMember(Name = "reorder", Order=16)]
        public String Reorder { get; set; }

        [DataMember(Name = "customizationEndDate", Order=17)]
        public String CustomizationEndDate { get; set; }

        [DataMember(Name = "self", Order=18)]
        public String Self { get; set; }
    }
}
