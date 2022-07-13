using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Net;
using System.Diagnostics;
using Disney.xBand.Simulator.Listeners;

namespace Disney.xBand.Simulator.Publishers
{
    public class HelloPublisher : Publisher
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private int helloCount;

        public event EventHandler StartStream;

        private string controllerUrl;

        public HelloPublisher(Dto.Reader reader, string controllerUrl, Dto.Repositories.IReaderRepository readerRepository)
            : base(reader, controllerUrl, readerRepository, 1000)
        {
            this.helloCount = 0;
            this.controllerUrl = controllerUrl;
        }

        protected override void TimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            if (helloCount % 30 == 0)
            {
                helloCount = 1;
                SendHello();
            }
            else
            {
                helloCount++;

                //if (cancellationToken.IsCancellationRequested)
                //{
                //    this.Stop();
                //}
            }
        }

        private void SendHello()
        {
            try
            {
                Dto.Hello response = this.ReaderRepository.GetHello(this.Reader.ReaderID);

                DataContractJsonSerializer s = new DataContractJsonSerializer(typeof(Dto.Hello));
                MemoryStream ms = new MemoryStream();
                s.WriteObject(ms, response);

                string json = Encoding.Default.GetString(ms.ToArray());
                ms.Close();

                Uri uri = new Uri(String.Format("{0}/hello", this.ControllerUrl));

                byte[] content = Encoding.UTF8.GetBytes(json);

                HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(uri);
                request.Method = "PUT";

                request.ContentType = "application/json";
                request.ContentLength = content.Length;

                try
                {
                    Stream requestStream = request.GetRequestStream();
                    requestStream.Write(content, 0, content.Length);
                    requestStream.Close();

                    HttpWebResponse resp = (HttpWebResponse)request.GetResponse();

                    if (resp.StatusCode != HttpStatusCode.OK)
                    {
                        //TODO: Assert failed.
                    }

                    resp.Close();
                    
                    log.Info(String.Format(
                        "Sent Hello to {0} from {1}",
                        uri.AbsoluteUri,
                        String.Format("Reader {0} Webport {1}", this.Reader.ReaderName,this.Reader.WebPort)));

                }
                catch (WebException e)
                {
                    //Cant't talk to xBRC
                    log.Error(String.Format("Hello message to {0} from {1} failed with status code: {2}.",
                        this.ControllerUrl,
                        String.Format("Reader {0} Port {1}", this.Reader.ReaderName, this.Reader.WebPort),
                        e.Status));
                }

                if (!String.IsNullOrEmpty(this.Reader.UpstreamUrl))
                {
                    if (this.StartStream != null)
                    {
                        this.StartStream(this, EventArgs.Empty);
                    }
                }

            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);
                log.Error("Unexpected Error sending to controller", ex);
            }
        }

    }
}
