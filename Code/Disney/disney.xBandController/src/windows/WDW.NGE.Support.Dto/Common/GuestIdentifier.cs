using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.Common
{
    /// <summary>
    /// Represents an identifier of a guest.
    /// </summary>
    [DataContract]
    public class GuestIdentifier
    {
        /// <summary>
        ///     The type of identifier, i.e. xid, transactional-guest-id, etc.
        /// </summary>
        [DataMember(Name = "type")]
        public string IdentifierType { get; set; }

        /// <summary>
        ///     The value of the identifier of the guest.
        /// </summary>
        [DataMember(Name="value")]
        public string IdentifierValue { get; set; }

    }
}
