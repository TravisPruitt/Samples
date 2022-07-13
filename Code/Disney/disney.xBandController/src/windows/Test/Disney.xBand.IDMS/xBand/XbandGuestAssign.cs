using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.xBand
{
    [DataContract]
    public class XbandGuestAssign
    {
        [DataMember(Name = "xbandId")]
        public long xbandId { get; set; }

        [DataMember(Name = "guestId")]
        public long guestId { get; set; }
    }
}
