using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.IDMS
{
    /// <summary>
    ///     The guest locators, which map to the IdentifierType of a <see cref="GuestIdentifier"/>.
    ///     Result of the /guest/locators service endpoint in IDMS.
    /// </summary>
    [DataContract]
    public class GuestLocators
    {
        /// <summary>
        ///     List of identifier types supported by IDMS.
        /// </summary>
        [DataMember(Name = "guestLocators")]
        public List<String> GuestLocatorList { get; set; }
    }
}
