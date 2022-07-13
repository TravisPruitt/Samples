using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Net.NetworkInformation;

namespace com.disney.xband.xbrc.XBRCInfo
{
    public class XBRCUtil
    {
        private string sMacAddress = null;
        private string sXBRMSUrl = null;
        private string sXBRCUrl = null;
        private int nLocationId = -1;
        private int eno = 0;

        private bool bSentHello = false;

        /*
         * Note that this enum is synchronized with Java code. Do not mess with it!
         */
        public enum LocationType : int 
        {
            Unspecified = 0,
            Entrance = 1,
            Waypoint = 2,
            Exit = 3,
            Load = 4,
            InCar = 5,
            Merge = 6,
            xPassEntry = 7,
            Combo = 8
        }

        // pass null for the xBRMS if don't know its URL or if you will connect to a 
        // specific URL. 
        public XBRCUtil(string sXBRMSUrl)
        {
            // get our own mac address
            sMacAddress = getMacAddress();
            if (sMacAddress == null)
                throw new ApplicationException("Could not retrieve device's MAC address");

            if (sXBRMSUrl == null)
            {
                // no help provided. Guess.
                sXBRMSUrl = "http://xbrms:8080/XBRMS/rest";
            }
            this.sXBRMSUrl = sXBRMSUrl;
        }

        // use this form if you know the URL of the xBRC you want to talk to and just
        // want to register at the first location with the given locationType
        public void Initialize(string sXBRCUrl, LocationType locationType)
        {
            this.sXBRCUrl = sXBRCUrl;

            // if the reader type was specified, look for a location of the given type
            if (locationType != LocationType.Unspecified)
            {
                // map the reader type to a location type id
                LocationInfo[] ali = GetLocationInfo();
                foreach (LocationInfo li in ali)
                {
                    if (li.type == locationType.ToString())
                    {
                        // found one that works
                        nLocationId = int.Parse(li.id);
                        return;
                    }
                }
                throw new ApplicationException("No location of the indicated type was found in the xBRC");
            }
        }

        // Use this form if you know the xBRC you want to talk to, but will specify the location id
        // explicitly
        public void Initialize(string sXBRCUrl)
        {
            Initialize(sXBRCUrl, LocationType.Unspecified);
        }

        // use this form if you want to look up the xBRC in the xBRMS and will specify the location
        // id explicitly
        public void Initialize(long lEntertainmentId)
        {
            Initialize(lEntertainmentId, LocationType.Unspecified);
        }

        // use this form if you want to look up the xBRC in the xBRMS and just want to register
        // at the first location with the indicated locationType
        public void Initialize(long lEntertainmentId, LocationType locationType)
        {
            // fetch the xbrc url from the entertainment id
            sXBRCUrl = findXBRC(lEntertainmentId);
            if (sXBRCUrl == null)
                throw new ApplicationException("Failed to get xBRC URL from xBRMS");

            Initialize(sXBRCUrl, locationType);
        }

        // returns information about the available facilities
        public Facility[] GetFacilities()
        {
            HttpChannel chan = new HttpChannel(sXBRMSUrl);

            string sData = chan.get("XBRMS/rest/facilities");
            if (sData==null)
                throw new ApplicationException("Failed to get facility information from xBRMS");

            StringReader sr = new StringReader(sData);
            XmlSerializer xs = new XmlSerializer(typeof(xbrms));
            xbrms x = xs.Deserialize(sr) as xbrms;
            if (x == null)
                throw new ApplicationException("Failed to deserialize facility information from xBRMS");

            return x.facilities;
        }

        // Returns information about all the locations present in the xBRC
        public LocationInfo[] GetLocationInfo()
        {
            if (sXBRCUrl == null)
                throw new ApplicationException("XBRC class must first be initialized");

            HttpChannel chan = new HttpChannel(sXBRCUrl);
            string sData = chan.get("readerlocationinfo");
            if (sData == null)
                throw new ApplicationException("xBRC is not responding");
            StringReader sr = new StringReader(sData);
            XmlSerializer xs = new XmlSerializer(typeof(venue));
            venue v = xs.Deserialize(sr) as venue;
            return v.readerlocationinfo;
        }

        // Explicitly identify the location id for the mobile gxp reader
        public void SetLocationId(int nLocationId)
        {
            this.nLocationId = nLocationId;
        }

        // send a tap using the designated id
        public void SendTap(string sID)
        {
            if (sXBRCUrl == null)
                throw new ApplicationException("XBRC class must first be initialized");

            if (nLocationId == -1)
                throw new ApplicationException("No location information has been provided in Initialize or through SetLocationId");

            // send a hello message if we haven't sent one before
            if (!bSentHello)
                sendHello();

            // now send the tap information
            string s = string.Format(@"{{
	                                        ""reader name"" : ""{0}"",
	                                        ""events"" :
                                            [
                                                {{
                                                    ""type"" : ""RFID"",
                                                    ""eno"" : {1},
                                                    ""time"" : ""{2}"",
                                                    ""uid"" : ""{3}""
                                                }}
                                            ]
                                        }}",
                                        sMacAddress,
                                        eno++,
                                        formatTime(DateTime.Now.ToUniversalTime()),
                                        sID);

            HttpChannel chan = new HttpChannel(sXBRCUrl);
            chan.put("/stream", s);


        }

        private void sendHello()
        {
            HttpChannel chan = new HttpChannel(sXBRCUrl);

            string s = string.Format(   @"{{
	                                        ""mac"" : ""{0}"",
	                                        ""port"" : 0,
	                                        ""next eno"" : {1},
	                                        ""reader name"" : ""{0}"",
	                                        ""reader type"" : ""Mobile Gxp"",
	                                        ""location id"" : {2}
                                        }}",
                                        sMacAddress,
                                        eno,
                                        nLocationId);
            chan.put("/hello", s);

            bSentHello = true;
        }

        private string findXBRC(long lEntertainmentId)
        {
            Facility[] af = GetFacilities();

            foreach (Facility f in af)
            {
                if (f.id == lEntertainmentId.ToString())
                    return f.url;
            }

            return null;
        }

        private string getMacAddress()
        {
            IPGlobalProperties computerProperties = IPGlobalProperties.GetIPGlobalProperties();
            NetworkInterface[] nics = NetworkInterface.GetAllNetworkInterfaces();

            foreach (NetworkInterface adapter in nics)
            {
                // ignore loopback
                if (adapter.NetworkInterfaceType == NetworkInterfaceType.Loopback)
                    continue;

                // return first non loopback
                return adapter.GetPhysicalAddress().ToString();
            }

            return null;
        }

        private string formatTime(DateTime dt)
        {
            return dt.ToString("yyyy-MM-ddTHH:mm:ss.FFF");
        }


    }
}
