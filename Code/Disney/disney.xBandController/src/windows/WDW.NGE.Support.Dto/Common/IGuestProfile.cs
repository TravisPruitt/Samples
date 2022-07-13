using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.Common
{
    /// <summary>
    ///     Common interfact for defining a guest profile between IDMS and OneView.
    /// </summary>
    public interface IGuestProfile
    {
        /// <summary>
        ///     Last name of the guest.
        /// </summary>
        String LastName { get; set; }
        
        /// <summary>
        ///     First name of the guest.
        /// </summary>
        String FirstName { get; set; }

        /// <summary>
        ///     A list of identifiers of a guest, <see cref="GuestIdentifier"/>.
        /// </summary>
        List<Common.GuestIdentifier> GuestIdentifiers { get; set; }
    }
}
