using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.OneView
{
    [DataContract]
    public class EntitlementLinks
    {
        [DataMember(Name = "self", Order = 1)]
        public Common.Link Self { get; set; }
    }
}
