using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class Address
    {
        [DataMember(Name="state", Order=1)]
        public String State { get; set;}

        [DataMember(Name="country", Order=2)]
        public String Country { get; set; }

        [DataMember(Name="address1", Order=3)]
        public String Address1 { get; set; }

        [DataMember(Name="address2", Order=4)]
        public String Address2 { get; set; }

        [DataMember(Name="city", Order=5)]
        public String City { get; set; }

        [DataMember(Name="postalCode", Order=6)]
        public String PostaCode { get; set; }
    }
}
