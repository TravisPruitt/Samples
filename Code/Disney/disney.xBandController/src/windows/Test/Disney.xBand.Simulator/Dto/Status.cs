using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Status : IJsonObject
    {
        [DataMember(Name = "count", Order = 1)]
        public int Count { get; set; }

        [DataMember(Name = "earliest", Order = 2)]
        public string EarliestEvent { get; set; }

        [DataMember(Name = "latest", Order = 3)]
        public string MostRecentEvent { get; set; }

        [DataMember(Name = "streamurl", Order = 4)]
        public string StreamUrl { get; set; }
    }

}
