using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class xViewLocation
    {
        [DataMember(Name = "xviewlocationid", Order = 1)]
        public int xViewLocaitonID { get; set; }

        [DataMember(Name = "xviewurl", Order = 2)]
        public string xViewURL { get; set; }

        [DataMember(Name = "xviewfriendlyname", Order = 3)]
        public string xViewFriendlyName { get; set; }

    }
}
