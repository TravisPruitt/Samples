using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.Parties
{
    public interface IPartyDao
    {
        Party Get(long partyId, Metrics metrics);

        Party Get(string partyName, Metrics metrics);

        long Save(Party party, Metrics metrics);

        long Assign(GuestPartyAssign guestPartyAssign, Metrics metrics);
    }
}
