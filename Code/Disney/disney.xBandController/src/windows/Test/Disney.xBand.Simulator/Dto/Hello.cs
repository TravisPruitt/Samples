using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Hello
    {
        [DataMember(Name = "mac", Order = 1)]
        public string MacAddress { get; set; }

        [DataMember(Name = "port", Order = 2)]
        public int Webport { get; set; }

        [DataMember(Name = "reader name", Order = 3)]
        public string ReaderName { get; set; }

        [DataMember(Name = "next eno", Order = 4)]
        public int NextEventNumber { get; set; }

        [DataMember(Name = "reader type", Order = 5)]
        public string ReaderType { get; set; }

        [DataMember(Name = "reader version", Order = 6)]
        public string ReaderVersion { get; set; }

        [DataMember(Name = "linux version", Order = 7)]
        public string LinuxVersion { get; set; }
    }
}
