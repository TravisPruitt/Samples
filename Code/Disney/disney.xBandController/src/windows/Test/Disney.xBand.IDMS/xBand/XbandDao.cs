using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Net;
using System.IO;
using System.Runtime.Serialization.Json;
using Disney.xBand.IDMS.Dao;

namespace Disney.xBand.IDMS.xBand
{
    public class XbandDao : DaoBase,  IXbandDao
    {
        public XbandDao(string rootUrl) : base(rootUrl)
        {
        }

        public Xband FindByXbandId(string xbandId, Metrics metrics)
        {
            return GetRequest<Xband>(String.Format(String.Concat(this.RootUrl, "xband/id/{0}"), xbandId), metrics);
        }
        
        public Xband FindByLongRangeId(string longRangeId, Metrics metrics)
        {
            return GetRequest<Xband>(String.Format(String.Concat(this.RootUrl, "xband/lrid/{0}"), longRangeId), metrics);
        }

        public Xband FindByBandId(string bandId, Metrics metrics)
        {
            return GetRequest<Xband>(String.Format(String.Concat(this.RootUrl, "xband/bandid/{0}"), bandId), metrics);
        }

        public Xband FindByTapId(string tapId, Metrics metrics)
        {
            return GetRequest<Xband>(String.Format(String.Concat(this.RootUrl, "xband/tapid/{0}"), tapId), metrics);
        }

        public Xband FindBySecureId(string secureId, Metrics metrics)
        {
            return GetRequest<Xband>(String.Format(String.Concat(this.RootUrl, "xband/secureid/{0}"), secureId), metrics);
        }

        public Xband FindByPublicId(string publicId, Metrics metrics)
        {
            return GetRequest<Xband>(String.Format(String.Concat(this.RootUrl, "xband/public/{0}"), publicId), metrics);
        }

        public void Assign(long xbandId, long guestId, Metrics metrics)
        {
            XbandGuestAssign assign = new XbandGuestAssign()
            {
                xbandId = xbandId,
                guestId = guestId
            };
            
            PostRequest<XbandGuestAssign>(String.Concat(this.RootUrl, "xband/assign"), metrics, assign);
        }

    }
}
