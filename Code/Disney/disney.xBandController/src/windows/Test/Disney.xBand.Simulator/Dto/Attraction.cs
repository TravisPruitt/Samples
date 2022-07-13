using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Attraction
    {
        [DataMember(Name="id", Order=1)]
        public int AttractionID { get; set; }

        [DataMember(Name = "name", Order = 2)]
        public string AttractionName { get; set; }

        [DataMember(Name = "mergeratio", Order = 3)]
        public decimal MergeRatio { get; set; }

        [DataMember(Name = "guestsperhour", Order = 4)]
        public int GuestsPerHour { get; set; }

        [DataMember(Name = "controller", Order = 5)]
        public Controller Controller { get; set; }

    }
}