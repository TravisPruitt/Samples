using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [DataContract]
    public class RequestAddress
    {
        [DataMember(Name = "address", Order = 1)]
        public Address Address { get; set; }

        [DataMember(Name = "confirmedAddress", Order = 2)]
        public Boolean confirmedAddress { get; set; }

        [DataMember(Name = "phoneNumber", Order = 3)]
        public String PhoneNumber { get; set; }
    }
}
