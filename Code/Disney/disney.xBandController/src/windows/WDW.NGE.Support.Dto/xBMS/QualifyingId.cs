using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.xBMS
{
    [DataContract]
    public class QualifyingId
    {
        [DataMember(Name = "qualifyingIdType", Order=1)]
        public String QualifyingIdType { get; set; }

        [DataMember(Name = "qualifyingId", Order=2)]
        public String QualifyingIdValue { get; set; }
    }
}
