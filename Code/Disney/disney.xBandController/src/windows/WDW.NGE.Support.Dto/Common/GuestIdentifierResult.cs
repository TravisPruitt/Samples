using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.Common
{
    /// <summary>
    ///     Result of calling the /identifiers endpoint in IDMS or OneView.
    /// </summary>
    [DataContract]
    public class GuestIdentifierResult
    {
        /// <summary>
        ///     A list of identifiers of a guest, <see cref="GuestIdentifier"/>.
        /// </summary>
        [DataMember(Name = "identifiers")]
        public List<Common.GuestIdentifier> Identifiers { get; set; }
    }
}
