using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Provisionator.Dto
{
    [DataContract]
    public class OffersetRequest
    {
        [DataMember(Name = "scheduleDate", Order = 1)]
        public string ScheduleDate { get; set; }

        [DataMember(Name = "parkId", Order = 2)]
        public string ParkID { get; set; }

        [DataMember(Name = "preferredEntertainments", Order = 3)]
        public int[] PreferredEntertainments { get; set; }

        [DataMember(Name = "reqGuestId", Order = 4)]
        public string RequestingXID { get; set; }

        [DataMember(Name = "guestIDs", Order = 5)]
        public string[] XIDs { get; set; }

    }

    [DataContract]
    public class BookOffersetRequest
    {
        [DataMember(Name = "tradeDatesMap", Order = 1)]
        public string[] TradesDateMap { get; set; }

        public BookOffersetRequest()
        {
        }
    }

}
