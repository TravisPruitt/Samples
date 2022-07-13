using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace xTPManufacturerTest
{
    class Http
    {
        static public bool sendRequest(string url, string method = "PUT")
        {
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(url);
            request.Method = method;

            try
            {
                using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                {
                    return (response.StatusCode == HttpStatusCode.OK);
                }
            }
            catch (Exception)
            {
                return false;
            }
        }

        static public bool sendFile(string url, string path, out int statusCode, out string reply)
        {
            try
            {
                HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(url);
                request.Method = "PUT";
                string extension = Path.GetExtension(path).ToLower().TrimStart('.');
                request.ContentType = GetMimeType(extension);
                request.SendChunked = false;

    //            request.ContentLength = TBD;

                FileStream fin = new FileStream(path, FileMode.Open);
                BinaryReader br = new BinaryReader(fin);
                BinaryWriter bw = new BinaryWriter(request.GetRequestStream());

                byte[] b;
                do
                {
                    b = br.ReadBytes(512);
                    bw.Write(b);
                } while (b.Length > 0);

                br.Close();
                bw.Close();

                HttpWebResponse response;
                try
                {
                    response = (HttpWebResponse)request.GetResponse();
                }
                catch (WebException ex)
                {
                    response = (HttpWebResponse)ex.Response;
                }

                statusCode = (int)response.StatusCode;
                reply = response.StatusDescription;

                return (response.StatusCode == HttpStatusCode.OK);
            }
            catch (Exception ex)
            {
                reply = "Exception during HTTP request, " + ex.Message;
                statusCode = 0;
                return false;
            }
        }


        public static string GetMimeType(string extension)
        {
            string mime = "application/octet-stream";
            MemoryStream ms = new MemoryStream(Resources.mime);
            StreamReader sr = new StreamReader(ms);
            extension = extension.ToLower().TrimStart('.');

            while (!sr.EndOfStream)
            {
                string line = sr.ReadLine();
                if (line.StartsWith("#") || line.Trim() == string.Empty) continue;
                string[] splitted = line.Split('\t');
                string[] extensions = splitted[splitted.Length - 1].Split(' ');
                if (extensions.Contains(extension.ToLower()))
                {
                    mime = splitted[0];
                    break;
                }
            }
            sr.Close();
            return mime;
        }

    }
}
