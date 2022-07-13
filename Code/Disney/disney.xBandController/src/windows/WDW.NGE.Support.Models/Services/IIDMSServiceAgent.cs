using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models.Services
{
    public interface IIDMSServiceAgent : IGuestServiceAgent
    {
        Models.Common.GuestIdentifierResult GetGuestIdentifiers(string guestIdType, string guestIdValue);

        void RemoveGuestIdentifier(long guestId, Models.Common.GuestIdentifier guestIdentifier);

        void MoveIdentifier(long guestId, Models.Common.GuestIdentifier guestIdentifier);

        void UpdateName(long guestId, string firstName, string lastName);

        void AssignBand(long guestId, string xbmsid);

        void CreateXbandAssociation(Models.IDMS.XbandAssociation xbandAssociation);

        List<String> GetVisualIds(string date);

        ServiceResult<Models.IDMS.GuestLocators> GetGuestLocators();
    }
}
