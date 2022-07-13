using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace com.disney.xband.xbrc.xBRCLab
{
    class XBrcChannel
    {
        private string sXbrcIPAddress;

        public XBrcChannel(string sXbrcIPAddress)
        {
            this.sXbrcIPAddress = sXbrcIPAddress;
        }

        public string get(string sPathAndArgs)
        {
            try
            {
                HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create("http://" + sXbrcIPAddress + ":8080/" + sPathAndArgs);
                HttpWebResponse res = (HttpWebResponse)req.GetResponse();
                StreamReader sr = new StreamReader(res.GetResponseStream());
                string sData = sr.ReadToEnd().Trim();
                sr.Close();
                res.Close();
                return sData;

            }
            catch (Exception)
            {
                return null;
            }
        }
    }
}
