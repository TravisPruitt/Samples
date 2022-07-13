using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.GxP
{
    /// <summary>
    ///     Result of a call to the Fast Pass+ system to determine the eligibility of a guest.
    ///     See the GxP service definitions for additional detail.
    /// </summary>
    [DataContract]
    public class EligibilityResult 
    {
        /// <summary>
        ///     Indicate whether the guest is eligible to selet entitlements in the speicified park.
        /// </summary>
        [DataMember(Name = "eligiblePark", Order = 1)]
        public Boolean EligiblePark { get; set; }

        /// <summary>
        ///     Messages codes indicating the reasons a guest is, or isn't eligible to select entitlements.
        /// </summary>
        [DataMember(Name = "messages", Order = 2)]
        public List<String> Messages { get; set; }

        /// <summary>
        ///     Identifier for the park.
        /// </summary>
        [DataMember(Name = "parkId", Order = 3)]
        public long ParkId { get; set; }

    }
}
