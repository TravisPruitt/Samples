using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.IDMS.GuestIdentifiers;
using Disney.xBand.IDMS.Celebrations;
using Disney.xBand.IDMS.Parties;

namespace Disney.xBand.IDMS.Guests
{
    public interface IGuestDao
    {
        long Save(Guest guest, Metrics metrics);

        long Update(GuestPut guest, Metrics metrics);

        long Update(GuestEmail guestEmail, Metrics metrics);

        GuestProfile FindByGuestId(long guestId, Metrics metrics);

        GuestProfile FindByGuestIdentifier(String guestIdType, String guestIdValue, Metrics metrics);

        List<GuestIdentifier> FindIdentifiers(string identifier, Metrics metrics);

        CelebrationCollection GetCelebrations(long guestId, Metrics metrics);

        Party GetParty(long guestId, Metrics metrics);

        Guest FindByEmailAddress(string emailAddress, Metrics metrics);
 
        List<GuestNameSearch> FindByName(string name, Metrics metrics);
    }
}
