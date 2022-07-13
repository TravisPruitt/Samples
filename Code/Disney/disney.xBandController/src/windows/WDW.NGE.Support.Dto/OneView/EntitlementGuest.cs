using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.OneView
{
    [DataContract]
    public class EntitlementGuest
    {
        [DataMember(Name = "roleOnEntitlement", Order = 1)]
        public List<String> EntitlementRoles { get; set; }

        [DataMember(Name="profile", Order = 2)]
        public GuestProfile Profile { get; set; }

    }
}
