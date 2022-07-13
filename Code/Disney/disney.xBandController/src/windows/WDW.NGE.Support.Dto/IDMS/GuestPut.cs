using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.IDMS
{
    [DataContract]
    public class GuestPut
    {
        [DataMember(Name = "guestId")]
        public string GuestId;

        [DataMember(Name = "name")]
        public GuestName Name { get; set; }

        [DataMember(Name = "status")]
        public String Status { get; set; }
    }
}
