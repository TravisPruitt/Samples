using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class LongRangeReaderEvent
    {
        [DataMember(Name = "eno", Order = 1)]
        public long EventNumber { get; set; }

        [DataMember(Name = "XLRID", Order = 2)]
        public string BandID { get; set; }

        [DataMember(Name = "time", Order = 4)]
        public string TimeVal { get; set; }

        [DataMember(Name = "pno", Order = 3)]
        public int PacketSequence { get; set; }

        [DataMember(Name = "freq", Order = 5)]
        public int Frequency { get; set; }

        [DataMember(Name = "ss", Order = 6)]
        public int SignalStrength { get; set; }

        [DataMember(Name = "chan", Order = 7)]
        public int Channel { get; set; }
    }
}
