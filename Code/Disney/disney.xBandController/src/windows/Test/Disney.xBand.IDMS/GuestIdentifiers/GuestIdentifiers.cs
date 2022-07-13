using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.Links;

namespace Disney.xBand.IDMS.GuestIdentifiers
{
    //{"identifiers":[{"value":"3B5D581FC275439C86F882EE5D72F8E4","type":"xid"},{"value":"501050","type":"gxp-link-id"}],"links":{"self":{"href":"/guest/35001984/identifiers"},"ownerProfile":{"href":"guest/35001984/profile"}}}

    [DataContract]
    public class GuestIdentifiers
    {
        [DataMember(Name="identifiers")]
        List<GuestIdentifier> Identifiers { get; set; }

        [DataMember(Name = "links")]
        LinkCollection Links { get; set; }
    }
}
