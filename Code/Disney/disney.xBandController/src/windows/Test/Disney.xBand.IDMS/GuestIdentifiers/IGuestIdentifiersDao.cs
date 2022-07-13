using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.GuestIdentifiers
{
    public interface IGuestIdentifiersDao
    {
        GuestIdentifiers FindByGuestIdentifiersByIdentifier(string identifier);
        
        void Save(GuestIdentifierPut identifier, long guestId, Metrics metrics);
    }
}
