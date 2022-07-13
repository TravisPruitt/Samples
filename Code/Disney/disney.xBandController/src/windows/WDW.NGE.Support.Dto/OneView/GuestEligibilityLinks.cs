using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.OneView
{
    [DataContract]
    public class GuestEligibilityLinks
    {
        [DataMember(Name = "magicPlusParticipantStatusEvaluationRecords", Order = 1)]
        public Common.Link MagicPlusParticipantStatusEvaluationRecords { get; set; }
    }
}
