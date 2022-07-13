using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.Celebrations
{
    public class CelebrationDao : Dao.DaoBase, ICelebrationDao
    {
        public CelebrationDao(string rootUrl)
            : base(rootUrl)
        {
        }

        public Celebration Get(long celebrationId, Metrics metrics)
        {
            return GetRequest<Celebration>(String.Concat(this.RootUrl, String.Format("celebrations/id/{0}", celebrationId)), metrics);
        }

        public CelebrationCollection Get(string xid, Metrics metrics)
        {
            return GetRequest<CelebrationCollection>(String.Concat(this.RootUrl, String.Format("celebrations/{0}", xid)), metrics);
        }

        public CelebrationCollection GetByGuestId(long guestId, Metrics metrics)
        {
            return GetRequest<CelebrationCollection>(String.Concat(this.RootUrl, String.Format("celebrations/guestId/{0}", guestId)), metrics);
        }

        public long Save(Celebration celebration, Metrics metrics)
        {
            return PostRequest<Celebration>(String.Concat(this.RootUrl, "celebrations/"), metrics, celebration);
        }

        public long Update(Celebration celebration, Metrics metrics)
        {
            return PutRequest<Celebration>(String.Concat(this.RootUrl, "celebrations/"), metrics, celebration);
        }
    }
}
