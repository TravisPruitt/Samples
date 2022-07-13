using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class ReaderLocation
    {
        [DataMember(Name = "ReaderLocationId", Order = 1)]
        public int ReaderLocationID { get; set; }

        [DataMember(Name = "ReaderLocationName", Order = 2)]
        public string ReaderLocationName { get; set; }

        [DataMember(Name = "ReaderLocationType", Order = 3)]
        public ReaderLocationType ReaderLocationType { get; set; }

        [DataMember(Name = "Controller", Order = 4)]
        public Controller Controller { get; set; }

        [DataMember(Name = "Readers", Order = 5)]
        public List<Reader> Readers { get; set; }
    }
}
