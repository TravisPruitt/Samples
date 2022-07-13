using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models.Services
{
    public interface IOneViewServiceAgent : IGuestServiceAgent
    {
        Models.OneView.GuestProfile GetGuestProfile(string guestIdType, string guestIdValue);

        Models.Common.GuestIdentifierResult GetGuestIdentifiers(string guestIdType, string guestIdValue);

        Models.OneView.Entitlement GetEntitlement(string reservationId);
    }
}
