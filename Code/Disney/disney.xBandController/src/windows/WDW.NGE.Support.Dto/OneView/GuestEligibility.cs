using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.OneView
{
    [DataContract]
    public class GuestEligibility
    {
        [DataMember(Name = "magicPlusParticipantStatus", Order = 1)]
        public String MagicPlusParticipantStatus { get; set; }
        
        [DataMember(Name = "magicPlusParticipantStatusEffectiveDate", Order = 2)]
        public String MagicPlusParticipantStatusEffectiveDate { get; set; }

        [DataMember(Name = "links", Order = 3)]
        public GuestEligibilityLinks Links { get; set; }

    }
}
