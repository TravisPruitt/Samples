using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    /*
    { "assignmentDateTime" : "2011-02-22T04:09:47Z",
     "externalNumber" : 4320000000000000,
     "printedName" : "Carol Xband3",
     "productId" : "B11012",
     "publicId" : 1234,
     "secondaryState" : "ORIGINAL",
     "secureId" : 66,
     "self" : "/xband/0192B866-85BD-4369-9E85-84237315A110",
     "shortRangeTag" : 166,
     "state" : "ACTIVE",
     "guestId" : "0915EFB4-3B7D-45B4-93FD-D7145941D484",
     "guestIdType" : "swid",
     "xbandId" : "0192B866-85BD-4369-9E85-84237315A110",
     "xbandRequest" : "/xband-requests/6315A9F3-C19C-4530-8337-A73AA21C33D3",
     "xbandOwnerId" : "6422FA29-6BDB-4831-9F5F-6A2A165E3FA1",
     "options" : "/xband-options/0192B866-85BD-4369-9E85-84237315A110"
     "history" : "/xband-history/0192B866-85BD-4369-9E85-84237315A110"}
     "bandRole" : "Puck"
    */

    [DataContract]
    public class xBand
    {
        [DataMember(Name = "assignmentDateTime", Order = 1)]
        public String AssignmentDateTime { get; set; }

        [DataMember(Name = "externalNumber", Order = 2)]
        public String ExternalNumber { get; set; }

        [DataMember(Name = "printedName", Order = 3)]
        public String PrintedName { get; set; }

        [DataMember(Name = "productId", Order = 4)]
        public String ProductId { get; set; }

        [DataMember(Name = "publicId", Order = 5)]
        public long PublicId { get; set; }

        [DataMember(Name = "secondaryState", Order = 6)]
        public String SecondaryState { get; set; }

        [DataMember(Name = "secureId", Order = 7)]
        public long SecureId { get; set; }

        [DataMember(Name = "self", Order = 8)]
        public String Self { get; set; }

        [DataMember(Name = "shortRangeTag", Order = 9)]
        public long ShortRangeTag { get; set; }

        [DataMember(Name = "state", Order = 10)]
        public String State { get; set; }

        [DataMember(Name = "guestId", Order = 11)]
        public String GuestId { get; set; }

        [DataMember(Name = "guestIdType", Order = 12)]
        public String GuestIdType { get; set; }

        [DataMember(Name = "xbandId", Order = 13)]
        public String XBandId { get; set; }

        [DataMember(Name = "xbandOwnerId", Order = 14)]
        public String XBandOwnerId { get; set; }

        [DataMember(Name = "options", Order = 15)]
        public String Options { get; set; }

        [DataMember(Name = "history", Order = 16)]
        public String History { get; set; }

        [DataMember(Name = "bandRole", Order = 17)]
        public String BandRole { get; set; }

        [IgnoreDataMember]
        public String XBandRequestId { get; set; }

        [IgnoreDataMember]
        public MessageState MessageState { get; set; }
    }
}
