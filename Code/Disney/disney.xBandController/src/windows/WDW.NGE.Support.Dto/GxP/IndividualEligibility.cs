using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.GxP
{
    /// <summary>
    ///     Result of the a call the the Fast Pass + individual eligiblity service.
    ///     Also used for individuals when calling the group eligiblity service.
    /// </summary>
    [DataContract]
    public class IndividualEligibility
    {
        /// <summary>
        ///     gxp-link-id (or is it xid?) for the guest.
        /// </summary>
        [DataMember(Name = "guestId", Order = 1)]
        public string GuestId { get; set; }

        /// <summary>
        ///     Window in which the entitlement is to be booked?
        /// </summary>
        [DataMember(Name = "bookingWindow", Order = 2)]
        public BookingWindow BookingWindow { get; set; }

        /// <summary>
        ///     Represents the elibility of the guest returned from the Fast Pass+ system.
        /// </summary>
        [DataMember(Name = "eligibilityResult", Order = 3)]
        public List<EligibilityResult> EligibilityResults { get; set; }
    }
}
