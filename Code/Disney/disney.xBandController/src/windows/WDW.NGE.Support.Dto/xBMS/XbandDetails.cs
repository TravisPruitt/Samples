using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    /*{

    "state": "ACTIVE",
    "options": "/xband-options/6DE37139-F959-4DB3-A9D9-DE615FEBCEC9",
    "publicId": 8199612,
    "productId": "C00300",
    "xbandOwnerId": "07274979-CC06-4B95-9BE7-ADA1BBD3973B",
    "secondaryState": "ORIGINAL",
    "externalNumber": "133362250334",
    "secureId": 1008597917268866,
    "shortRangeTag": 36076674938865410,
    "guestId": "31559998",
    "guestIdType": "transactional-guest-id",
    "printedName": "MCKENNA",
    "xbandId": "6DE37139-F959-4DB3-A9D9-DE615FEBCEC9",
    "xbandRequest": "/xband-requests/1E26EFCC-9765-491B-84D5-757B94317621",
    "assignmentDateTime": "2012-11-16T02:56:25Z",
    "history": "/xband-history/6DE37139-F959-4DB3-A9D9-DE615FEBCEC9",
    "self": "/xband/6DE37139-F959-4DB3-A9D9-DE615FEBCEC9"
}*/

    [DataContract]
    public class XbandDetails
    {
        [DataMember(Name = "state")]
        public String State { get; set; }

        [DataMember(Name = "options")]
        public String Options { get; set; }

        [DataMember(Name = "publicId")]
        public long PublicId { get; set; }

        [DataMember(Name = "productId")]
        public String ProductId { get; set; }

        [DataMember(Name = "xbandOwnerId")]
        public String xBandOwnerId { get; set; }

        [DataMember(Name = "secondaryState")]
        public String SecondaryState { get; set; }

        [DataMember(Name = "externalNumber")]
        public String ExternalNumber { get; set; }

        [DataMember(Name = "secureId")]
        public long SecureId { get; set; }

        [DataMember(Name = "shortRangeTag")]
        public long ShortRangeTag { get; set; }

        [DataMember(Name = "guestId")]
        public String GuestId { get; set; }

        [DataMember(Name = "guestIdType")]
        public String GuestIdType { get; set; }

        [DataMember(Name = "printedName")]
        public String PrintedName { get; set; }

        [DataMember(Name = "xbandId")]
        public String xBandId { get; set; }

        [DataMember(Name = "xbandRequest")]
        public String xBandRequest { get; set; }

        [DataMember(Name = "assignmentDateTime")]
        public String AssignmentDateTime { get; set; }

        [DataMember(Name = "history")]
        public String History { get; set; }

        [DataMember(Name = "self")]
        public String Self { get; set; }
    }
}
