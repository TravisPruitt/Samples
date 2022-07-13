using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.Celebrations
{
    public interface ICelebrationDao
    {
        Celebration Get(long celebrationId, Metrics metrics);

        CelebrationCollection GetByGuestId(long guestId, Metrics metrics);

        CelebrationCollection Get(string xid, Metrics metrics);

        long Save(Celebration celebration, Metrics metrics);

        long Update(Celebration celebration, Metrics metrics);
    }
}
