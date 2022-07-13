using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Reader
    {
        [DataMember(Name = "ReaderId", Order = 1)]
        public int ReaderID { get; set; }

        [DataMember(Name = "ReaderName", Order = 2)]
        public string ReaderName { get; set; }

        [DataMember(Name = "Webport", Order = 3)]
        public int WebPort { get; set; }

        [DataMember(Name = "Controller", Order = 4)]
        public Controller Controller { get; set; }

        [DataMember(Name = "Active", Order = 5)]
        public bool Active { get; set; }

        [DataMember(Name = "UpstreamUrl", Order = 6)]
        public string UpstreamUrl { get; set; }

        [DataMember(Name = "ReaderType", Order = 7)]
        public ReaderType ReaderType { get; set; }

        [DataMember(Name = "EventsInterval", Order = 8)]
        public int EventsInterval { get; set; }

        [DataMember(Name = "MaximumEvents", Order = 9)]
        public int MaximumEvents { get; set; }

        [DataMember(Name = "GuestCapacity", Order = 10)]
        public int GuestCapacity { get; set; }

        [DataMember(Name = "ReaderLocationType", Order = 11)]
        public ReaderLocationType ReaderLocationType { get; set; }

    }
}
