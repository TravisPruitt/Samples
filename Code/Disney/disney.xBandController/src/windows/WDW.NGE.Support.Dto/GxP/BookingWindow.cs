using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.GxP
{
    /// <summary>
    ///     Window in which an entitlement is booked.
    /// </summary>
    [DataContract]
    public class BookingWindow
    {
 
        /// <summary>
        ///     The time the entitlement is no longer valid.
        /// </summary>
        [DataMember(Name = "endTime", Order=1)]
        public String EndTime { get; set; }

        /// <summary>
        ///     The time the entitlement becomes valid.
        /// </summary>
        [DataMember(Name = "startTime", Order = 2)]
        public string StartTime { get; set; }
    }
}
