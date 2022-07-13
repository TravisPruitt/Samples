using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class ReaderType
    {
        [DataMember(Name = "ReaderTypeId", Order = 1)]
        public int ReaderTypeID { get; set; }

        [DataMember(Name = "ReaderTypeName", Order = 2)]
        public string ReaderTypeName { get; set; }

    }
}
