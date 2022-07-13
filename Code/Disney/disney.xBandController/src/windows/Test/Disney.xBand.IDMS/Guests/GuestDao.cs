using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Runtime.Serialization.Json;
using System.IO;
using Disney.xBand.IDMS.GuestIdentifiers;
using Disney.xBand.IDMS.Celebrations;
using Disney.xBand.IDMS.Parties;

namespace Disney.xBand.IDMS.Guests
{
    public class GuestDao : Dao.DaoBase, IGuestDao
    {
        public GuestDao(string rootUrl)
            : base(rootUrl)
        {
        }

        public GuestProfile FindByGuestIdentifier(String guestIdType, String guestIdValue, Metrics metrics)
        {
            return GetRequest<GuestProfile>(String.Format(String.Concat(this.RootUrl, "guest/id;{0}={1}"), guestIdType, guestIdValue), metrics);
        }

    }
}
