using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class ReaderName : IJsonObject
    {
        [DataMember(Name = "reader-name", Order = 1)]
        public string Name { get; set; }
    }
}
