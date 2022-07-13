using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.Parties
{
    public class PartyDao : Dao.DaoBase, IPartyDao
    {
        public PartyDao(string rootUrl)
            : base(rootUrl)
        {
        }

        public Party Get(long partyId, Metrics metrics)
        {
            return GetRequest<Party>(String.Concat(this.RootUrl, String.Format("party/{0}",partyId)), metrics);
        }

        public Party Get(string partyName, Metrics metrics)
        {
            return GetRequest<Party>(String.Concat(this.RootUrl, String.Format("party/search/{0}", partyName)), metrics);
        }

        public long Save(Party party, Metrics metrics)
        {
            return PostRequest<Party>(String.Concat(this.RootUrl, "party/"), metrics, party);
        }

        public long Assign(GuestPartyAssign guestPartyAssign, Metrics metrics)
        {
            return PostRequest<GuestPartyAssign>(String.Concat(this.RootUrl, "party/guest"), metrics, guestPartyAssign);
        }
    }
}
