using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.IDMS.GuestIdentifiers
{
    [DataContract]
    public class GuestIdentifierPut
    {
        [DataMember(Name="identifier-type")]
        public string  IdentifierType { get; set; }
	
        [DataMember(Name="identifier-value")]
        public string  IdentifierValue { get; set; }
    }
}
