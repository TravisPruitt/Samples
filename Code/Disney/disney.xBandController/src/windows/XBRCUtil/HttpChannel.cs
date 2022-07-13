using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace com.disney.xband.xbrc.XBRCInfo
{
    class HttpChannel
    {
        private string sURL;

        public HttpChannel(string sURL)
        {
            this.sURL = sURL;
        }

        public string get(string sPathAndArgs)
        {
            try
            {
                HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create(sURL + "/" + sPathAndArgs);
                req.Proxy = null;
                req.Timeout = 10000;
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

        public bool put(string sPathAndArgs, string sData)
        {
            try
            {

                HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create(sURL + "/" + sPathAndArgs);
                req.Method = "PUT";
                req.Proxy = null;
                req.Timeout = 10000;
                StreamWriter sw = new StreamWriter(req.GetRequestStream());
                sw.Write(sData);
                sw.Close();
                HttpWebResponse res = (HttpWebResponse)req.GetResponse();
                return res.StatusCode == HttpStatusCode.OK;
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}
