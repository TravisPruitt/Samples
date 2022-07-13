using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace Disney.xBand.IDMS.GuestIdentifiers
{
    public class GuestIdentifiersDao : Dao.DaoBase, IGuestIdentifiersDao
    {
        public GuestIdentifiersDao(string rootUrl)
            : base(rootUrl)
        {
        }

        public GuestIdentifiers FindByGuestIdentifiersByIdentifier(string identifier)
        {
            GuestIdentifiers guestIdentifiers = null;

            WebRequest webRequest = WebRequest.Create(String.Format(String.Concat(this.RootUrl, "guest/{0}/identifiers"), identifier));
            webRequest.Method = "GET";
            webRequest.ContentType = "application/json; charset=utf-8";

            using (HttpWebResponse webResponse = webRequest.GetResponse() as HttpWebResponse)
            {
                if (webResponse.StatusCode == HttpStatusCode.OK)
                {
                    using (StreamReader reader = new StreamReader(webResponse.GetResponseStream()))
                    {
                        string responseJson = reader.ReadToEnd();

                        guestIdentifiers = Serializer.Deserialize<GuestIdentifiers>(responseJson);
                    }
                }
            }

            return guestIdentifiers;
        }

        public void Save(GuestIdentifierPut guestIdentifier, long guestId, Metrics metrics)
        {
            PostRequest<GuestIdentifierPut>(String.Format(String.Concat(this.RootUrl, "guest/{0}/identifiers"),guestId), metrics, guestIdentifier);
        }

    }
}
