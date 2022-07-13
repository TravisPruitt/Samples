using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.Celebrations
{

    [DataContract]
    public class Celebration
    {
        [DataMember(Name = "active")]
        public Boolean Active { get; set; }

        [DataMember(Name = "guestId")]
        public long GuestID { get; set; }

        [DataMember(Name = "celebrationId")]
        public int CelebrationID { get; set; }

        [DataMember(Name = "type")]
        public string CelebrationType { get; set; }
    }
}
