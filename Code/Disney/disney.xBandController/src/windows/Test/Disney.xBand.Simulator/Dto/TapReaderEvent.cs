using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class TapReaderEvent
    {
        [DataMember(Name = "eno", Order = 1)]
        public long EventNumber { get; set; }

        [DataMember(Name = "XRFID")]
        public string BandID { get; set; }
        
        [DataMember(Name = "time")]
        public string TimeVal { get; set; }
    }
}
