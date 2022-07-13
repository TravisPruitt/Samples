using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Diagnostics;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Xml.Serialization;
using System.Xml;

namespace WDW.NGE.Support.Models.Services
{
    public class ServiceAgent
    {
        private Metrics metrics;

        protected string RootUrl { get; private set; }

        public ServiceAgent(string rootUrl, string metricsTitle)
        {
            this.RootUrl = rootUrl;
            this.metrics = new Metrics(metricsTitle);
        }

        public ServiceResult<T> GetRequest<T>(string url)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();

            ServiceResult<T> serviceResult = new ServiceResult<T>()
            {
                Result = default(T)
            };

            WebRequest webRequest = WebRequest.Create(url);
            webRequest.Method = "GET";
            //Set timeout to 10 seconds.
            webRequest.Timeout = 10000;

            try
            {
                using (HttpWebResponse webResponse = webRequest.GetResponse() as HttpWebResponse)
                {
                    serviceResult.SetStatus(webResponse.StatusCode);

                    if (webResponse.StatusCode == HttpStatusCode.OK)
                    {
                        using (StreamReader reader = new StreamReader(webResponse.GetResponseStream()))
                        {

                            string responseJson = reader.ReadToEnd();

                            sw.Stop();
                            this.metrics.Update(sw);
                            using (MemoryStream memStream = new MemoryStream(Encoding.UTF8.GetBytes(responseJson)))
                            {

                                // deserialize the response 
                                DataContractJsonSerializer deserializer = new DataContractJsonSerializer(typeof(T));
                                serviceResult.Result = (T)deserializer.ReadObject(memStream);
                            }
                        }
                    }
                }
            }
            catch (WebException ex)
            {
                if (ex.Status == WebExceptionStatus.Timeout)
                {
                    serviceResult.Status = ServiceCallStatus.Timeout;
                }
                else
                {
                    if (ex.Message.Contains("404"))
                    {
                        serviceResult.Status = ServiceCallStatus.NotFound;
                    }
                    else
                    {
                        serviceResult.Status = ServiceCallStatus.Unknown;
                    }
                }
            }
            catch (IOException ex)
            {
                serviceResult.Status = ServiceCallStatus.Unknown;
            }
            catch (Exception)
            {
                serviceResult.Status = ServiceCallStatus.Unknown;
            }

            return serviceResult;
        }

        public T GetXmlRequest<T>(string url)
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
                        this.metrics.Update(sw);
                        using (MemoryStream memStream = new MemoryStream(Encoding.UTF8.GetBytes(responseXml)))
                        {

                            // deserialize the response 
                            // Create an instance of the XmlSerializer specifying type and namespace.
                            XmlSerializer serializer = new XmlSerializer(typeof(T));
                            
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

        public long PostRequest<T>(string url, T data)
        {
            return PutOrPostRequest<T>(url, data, "POST");
        }
        
        public long PutRequest<T>(string url, T data)
        {
            return PutOrPostRequest<T>(url, data, "PUT");
        }

        private long PutOrPostRequest<T>(string url, T data, string method)
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

            try
            {
                using (HttpWebResponse webResponse = webRequest.GetResponse() as HttpWebResponse)
                {
                    if (webResponse.StatusCode == HttpStatusCode.Created)
                    {
                        string location = webResponse.Headers["Location"];

                        if (!String.IsNullOrEmpty(location))
                        {
                            string resultString = location.Substring(location.LastIndexOf('/') + 1, location.Length - (location.LastIndexOf('/') + 1));

                            result = long.Parse(resultString);
                        }
                    }
                }
            }
            catch (Exception)
            {
            }

            sw.Stop();
            this.metrics.Update(sw);

            return result;
        }
    }
}
