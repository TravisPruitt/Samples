using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.IDMS
{
    [DataContract]
    public class GuestIdentifierPut
    {
        [DataMember(Name = "identifier-type")]
        public string IdentifierType { get; set; }

        [DataMember(Name = "identifier-value")]
        public string IdentifierValue { get; set; }
    }
}
