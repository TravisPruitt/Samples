using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.GxP
{
    /// <summary>
    ///     Result of a call the Fast Pass+ system to determine eligiblity of a group to select entitlements.
    /// </summary>
    [DataContract]
    public class GroupEligibility
    {
        /// <summary>
        ///     Eligibility of each individual.
        /// </summary>
        [DataMember(Name = "individuals", Order = 1)]
        public List<IndividualEligibility> Individuals { get; set; }

        /// <summary>
        ///     Eligbility for the entire group.
        /// </summary>
        [DataMember(Name = "groupEligibilityResult", Order = 2)]
        public List<EligibilityResult> GroupEligibilityResult { get; set; }
    }
}
