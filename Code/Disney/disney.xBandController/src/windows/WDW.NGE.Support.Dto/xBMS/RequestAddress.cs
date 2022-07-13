using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class RequestAddress
    {
        [DataMember(Name="address", Order=1)]
        public Address Address { get; set; }

        [DataMember(Name="confirmedAddress", Order=2)]
        public Boolean ConfirmedAddress { get; set; }

        [DataMember(Name="phoneNumber", Order=3)]
        public String PhoneNumber { get; set; }
    }
}
