using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class ReaderLocationType
    {
        [DataMember(Name = "ReaderLocationTypeId", Order = 1)]
        public int ReaderLocationTypeID { get; set; }

        [DataMember(Name = "ReaderLocationTypeName", Order = 2)]
        public string ReaderLocationTypeName { get; set; }

    }
}
