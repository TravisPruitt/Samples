using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

namespace com.disney.xband.xbrc.xBRCLab
{
    public class ReaderInfo
    {
        private string sURL;
        private venue venueInfo = null;

        public ReaderInfo(string sURL)
        {
            this.sURL = sURL;
        }

        public void fetch()
        {
            XBrcChannel chan = new XBrcChannel(sURL);
            string sReaderInfo = chan.get("/readerlocationinfo");
            StringReader sr = new StringReader(sReaderInfo);
            XmlSerializer ser = new XmlSerializer(typeof(venue));
            venueInfo = (venue) ser.Deserialize(sr);
        }

        public List<string> getLocations()
        {
            if (venueInfo == null)
                fetch();

            List<string> li = new List<string>();
            foreach (venueReaderlocation loc in venueInfo.readerlocationinfo)
                li.Add(loc.name);

            return li;
        }

        public List<string> getReaderNames()
        {
            if (venueInfo == null)
                fetch();

            List<string> li = new List<string>();
            foreach (venueReaderlocation loc in venueInfo.readerlocationinfo)
            {
                if (loc.name != "UNKNOWN")
                {
                    foreach (venueReaderlocationReader rdr in loc.readers)
                        if (rdr.type == "Long Range")
                            li.Add(rdr.name);
                }
            }

            return li;
        }

        public venueReaderlocationReader getReaderInfo(string sName)
        {
            if (venueInfo == null)
                fetch();

            List<string> li = new List<string>();
            foreach (venueReaderlocation loc in venueInfo.readerlocationinfo)
            {
                if (loc.name != "UNKNOWN")
                {
                    foreach (venueReaderlocationReader rdr in loc.readers)
                        if (rdr.name == sName)
                            return rdr;
                }
            }

            return null;
        }

    }
}
