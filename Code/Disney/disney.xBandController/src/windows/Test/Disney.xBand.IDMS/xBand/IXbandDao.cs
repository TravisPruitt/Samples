using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.xBand
{
    public interface IXbandDao
    {
        Xband FindByXbandId(string xbandId, Metrics metrics);

        Xband FindByLongRangeId(string longRangeId, Metrics metrics);

        Xband FindByBandId(string bandId, Metrics metrics);

        Xband FindByTapId(string tapId, Metrics metrics);

        Xband FindBySecureId(string secureId, Metrics metrics);

        Xband FindByPublicId(string publicId, Metrics metrics);

        void Assign(long xbandId, long guestId, Metrics metrics);
    }
}
