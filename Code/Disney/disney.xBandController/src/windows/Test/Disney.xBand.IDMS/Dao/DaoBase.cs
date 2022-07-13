using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Diagnostics;
using System.IO;
using System.Runtime.Serialization.Json;
using Disney.xBand.IDMS.Status;
using System.Xml.Serialization;
using System.Xml;

namespace Disney.xBand.IDMS.Dao
{
    public class DaoBase
    {
        protected string RootUrl { get; private set; }

        public DaoBase(string rootUrl)
        {
            this.RootUrl = rootUrl;
        }

        public T GetRequest<T>(string url, Metrics metrics)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();

            T result = default(T);

            WebRequest webRequest = WebRequest.Create(url);
            webRequest.Method = "GET";
            //Set timeout to 20 seconds.
            webRequest.Timeout = 20000;

            using (HttpWebResponse webResponse = webRequest.GetResponse() as HttpWebResponse)
            {
                if (webResponse.StatusCode == HttpStatusCode.OK)
                {
                    using (StreamReader reader = new StreamReader(webResponse.GetResponseStream()))
                    {

                        string responseJson = reader.ReadToEnd();

                        sw.Stop();
                        metrics.Update(sw);
                        using (MemoryStream memStream = new MemoryStream(Encoding.UTF8.GetBytes(responseJson)))
                        {

                            // deserialize the response 
                            DataContractJsonSerializer deserializer = new DataContractJsonSerializer(typeof(T));
                            result = (T)deserializer.ReadObject(memStream);
                        }
                    }
                }
            }

            return result;
        }

        public T GetXmlRequest<T>(string url, Metrics metrics)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();

            T result = default(T);

            WebRequest webRequest = WebRequest.Create(url);
            webRequest.Method = "GET";
            //Set timeout to 20 seconds.
            webRequest.Timeout = 20000;

            using (HttpWebResponse webResponse = webRequest.GetResponse() as HttpWebResponse)
            {
                if (webResponse.StatusCode == HttpStatusCode.OK)
                {
                    using (StreamReader reader = new StreamReader(webResponse.GetResponseStream()))
                    {

                        string responseXml = reader.ReadToEnd();

                        sw.Stop();
                        metrics.Update(sw);
                        using (MemoryStream memStream = new MemoryStream(Encoding.UTF8.GetBytes(responseXml)))
                        {

                            // deserialize the response 
                            // Create an instance of the XmlSerializer specifying type and namespace.
                            XmlSerializer serializer = new XmlSerializer(typeof(StatusResult));
                            
                            using(StringReader stringReader = new StringReader(responseXml))
                            {
                                using (XmlTextReader xmlReader = new XmlTextReader(stringReader))
                                {
                                    result = (T)serializer.Deserialize(xmlReader);
                                }
                            }
                        }
                    }
                }
            }

            return result;
        }

        public long PostRequest<T>(string url, Metrics metrics, T data)
        {
            return PutOrPostRequest<T>(url, metrics, data, "POST");
        }
        
        public long PutRequest<T>(string url, Metrics metrics, T data)
        {
            return PutOrPostRequest<T>(url, metrics, data, "PUT");
        }

        private long PutOrPostRequest<T>(string url, Metrics metrics, T data, string method)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();
            long result = -1;

            WebRequest webRequest = WebRequest.Create(url);
            webRequest.Method = method;

            DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(T));
            MemoryStream ms = new MemoryStream();
            serializer.WriteObject(ms, data);

            string json = Encoding.UTF8.GetString(ms.ToArray());
            ms.Close();
            byte[] byteArray = Encoding.UTF8.GetBytes(json);

            webRequest.ContentType = "application/json; charset=utf-8";
            webRequest.ContentLength = byteArray.Length;
            Stream dataStream = webRequest.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();

            using (HttpWebResponse webResponse = webRequest.GetResponse() as HttpWebResponse)
            {
                if (webResponse.StatusCode == HttpStatusCode.Created)
                {
                    string location = webResponse.Headers["Location"];

                    if(!String.IsNullOrEmpty(location))
                    {
                        string resultString = location.Substring(location.LastIndexOf('/') + 1, location.Length - (location.LastIndexOf('/') + 1) );

                        result = long.Parse(resultString);
                    }
                }
            }

            sw.Stop();
            metrics.Update(sw);

            return result;
        }
    }
}
