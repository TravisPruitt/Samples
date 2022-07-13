using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.Links
{
    [DataContract]
    public class LinkCollection
    {
        [DataMember(Name = "self")]
        public Link Self { get; set; }

        [DataMember(Name = "ownerProfile")]
        public Link OwnerProfile { get; set; }

        [DataMember(Name = "xbandRequest")]
        public Link xBandRequest { get; set; }

        [DataMember(Name = "productIdReference")]
        public Link ProductIdReference { get; set; }

        [DataMember(Name = "nextActions")]
        public Link NextActions { get; set; }

        [DataMember(Name = "history")]
        public Link History { get; set; }
    }
}
