using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;

namespace Disney.xBand.xTRC
{
    internal class HttpChannel
    {
        private string url;

        public HttpChannel(string url)
        {
            this.url = url;
        }

        internal string get(string sPathAndArgs)
        {
            try
            {
                HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create(url + "/" + sPathAndArgs);
                req.Proxy = null;
                req.Timeout = 10000;
                string data = String.Empty;
                using (HttpWebResponse res = (HttpWebResponse)req.GetResponse())
                {
                    using (StreamReader sr = new StreamReader(res.GetResponseStream()))
                    {
                        data = sr.ReadToEnd().Trim();
                    }
                }
                return data;

            }
            catch (Exception)
            {
                //TODO: Add some logging.
                return null;
            }
        }

        internal bool put(string sPathAndArgs, string sData)
        {
            try
            {

                HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create(url + "/" + sPathAndArgs);
                req.Method = "PUT";
                req.Proxy = null;
                req.Timeout = 10000;
                using (StreamWriter sw = new StreamWriter(req.GetRequestStream()))
                {
                    sw.Write(sData);
                }
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
