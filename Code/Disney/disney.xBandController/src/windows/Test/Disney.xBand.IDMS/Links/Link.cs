using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.Links
{
    [DataContract]
    public class Link
    {
        [DataMember(Name="href")]
        public String Href { get; set; }

        [DataMember(Name = "name")]
        public String Name { get; set; }
    }
}
