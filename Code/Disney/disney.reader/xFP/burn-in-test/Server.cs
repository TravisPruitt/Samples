using System;
using System.IO;
using System.Net;
using System.Web.Script.Serialization;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;
using System.Collections.Generic;

namespace xTPManufacturerTest
{
    class Server
    {
        private HttpListener listener;

        private MainForm form;
        private Object _helloLock = new Object();

        public Server(MainForm form)
        {
            this.form = form;
            listener = new HttpListener();
            listener.Prefixes.Add("http://*:8080/");
        }

        public void Start()
        {
            listener.Start();
            ListenForNextRequest();
            Trace.Write("Test server started");
        }

        public void Stop()
        {
            listener.Stop();
            Trace.Write("Test server stopped");
        }

        private void ListenForNextRequest()
        {
            listener.BeginGetContext(new AsyncCallback(ListenerCallback), listener);
        }

        private void ListenerCallback(IAsyncResult result)
        {
            HttpListener listener = (HttpListener)result.AsyncState;
            if (!listener.IsListening)
                return;

            HttpListenerContext context = listener.EndGetContext(result);
            ListenForNextRequest();

            try
            {
                handleRequest(context);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString());
            }
            finally
            {
                context.Response.Close();
            }
        }

        private void handleRequest(HttpListenerContext context)
        {
            switch (context.Request.Url.AbsolutePath)
            {
                case "/hello":
                    handleHello(context);
                    break;
                case "/stream":
                    handleStream(context);
                    break;
                default:
                    send404Reply(context.Response);
                    break;
            }
        }

        private void sendOkReply(HttpListenerResponse response)
        {
            response.StatusCode = 200;
            response.OutputStream.Flush();
            response.OutputStream.Close();
        }

        private void send404Reply(HttpListenerResponse response)
        {
            response.StatusCode = 404;
            sendStringReply(response, "File not found");
        }

        private void send400Reply(HttpListenerResponse response)
        {
            response.StatusCode = 400;
            sendStringReply(response, "Bad Request");
        }

        private void sendStringReply(HttpListenerResponse response, string msg)
        {
            response.ContentType = "text/plain";
            byte[] buf = System.Text.Encoding.UTF8.GetBytes(msg);
            response.ContentLength64 = buf.Length;
            response.OutputStream.Write(buf, 0, buf.Length);
            response.OutputStream.Close();
        }

        private dynamic parseJson(HttpListenerContext context)
        {
            StreamReader sr = new StreamReader(context.Request.InputStream);
            string jsonText = sr.ReadToEnd();
            var jss = new JavaScriptSerializer();
            return jss.Deserialize<dynamic>(jsonText);
        }

        private void handleHello(HttpListenerContext context)
        {
            var dict = parseJson(context);
            if (dict == null)
            {
                Trace.Write("Received malformed JSON in hello");
                send400Reply(context.Response);
                return;
            }

            bool isNewReader = false;
            Reader reader;
            lock (_helloLock)
            {
                reader = form.readerList.findMac(dict["mac"]);
                if (reader == null)
                {
                    reader = new Reader(dict, context.Request.RemoteEndPoint.Address.ToString());
                    form.addData(reader);
                    isNewReader = true;
                }
            }

            if (isNewReader)
                reader.init();

            reader.processHello(IPAddress, dict["next eno"]);

            sendOkReply(context.Response);
        }

        private void handleStream(HttpListenerContext context)
        {
//            Dictionary<string, object> d;

            var dict = parseJson(context);

            if (!dict.ContainsKey("events"))
            {
                Console.WriteLine("Events message contains no 'events' field");
                return;
            }

            var events = dict["events"];

            if (!dict.ContainsKey("reader name"))
            {
                Console.WriteLine("Events message contains no 'reader name' field");
                return;
            }

            string name = dict["reader name"].ToString();

            Reader reader = form.readerList.findMac(name);
            if (reader == null)
            {
                Console.WriteLine("No such reader found");
                return;
            }

            foreach (dynamic evt in events)
            {
                if (!evt.ContainsKey("type"))
                {
                    Console.WriteLine("Event has no 'type' field");
                }
                else
                {
                    string type = evt["type"];
                    switch (type)
                    {
                        case "xfp-diagnostics":
                            reader.processDiagnosticEvent(evt);
                            break;
                        case "RFID":
                            reader.processTap(evt);
                            break;
                        case "bio-enroll":
                        case "bio-match":
                            reader.processBioEnroll(evt);
                            break;
                        case "bio-scan-error":
                            reader.processBioScanError(evt);
                            break;
                        case "xbio-diagnostics":
                            reader.processXbioDiagnostics(evt);
                            break;
                        default:
                            reader.processUnknownEvent(evt);
                            break;
                    }
                }
            }

            sendOkReply(context.Response);
        }

    
        public string IPAddress
        {
            get
            {
                var addresses = Dns.GetHostAddresses(Dns.GetHostName());
                string localIp = string.Empty;
                foreach (IPAddress ad in addresses)
                {
                    localIp = ad.ToString();

                    if (ad.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                        break;
                }
                return localIp;
            }
        }
    }

}
