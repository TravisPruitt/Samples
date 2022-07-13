﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class LongRangeReaderEvents : IJsonObject
    {
        [DataMember(Name = "reader name", Order = 1)]
        public string ReaderName { get; set; }

        [DataMember(Name = "events", Order = 2)]
        public Dto.LongRangeReaderEvent[] ReaderEvents { get; set; }
    }

}
