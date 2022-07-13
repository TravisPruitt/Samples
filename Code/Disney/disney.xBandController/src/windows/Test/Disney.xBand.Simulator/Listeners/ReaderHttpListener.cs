using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Threading;
using System.Diagnostics;

namespace Disney.xBand.Simulator.Listeners
{
    public abstract class ReaderHttpListener : IListener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private HttpListener listener;

        protected Dto.Reader Reader { get; private set; }

        protected Dto.Repositories.IReaderRepository Repository { get; set; }

        public ReaderHttpListener(Dto.Reader reader, Dto.Repositories.IReaderRepository readerRepository, string suffix)
        {
            if (!HttpListener.IsSupported)
            {
                //TODO: Figure out what exception to throw.
            }

            // Create a listener.
            this.listener = new HttpListener();
            // Add the prefixes.
            if (suffix.Length == 0)
            {
                this.listener.Prefixes.Add(String.Format("http://+:{0}/", reader.WebPort));
            }
            else
            {
                this.listener.Prefixes.Add(String.Format("http://+:{0}/{1}/", reader.WebPort, suffix));

            }

            this.Reader = reader;

            this.Repository = readerRepository;
        }

        public void Start()
        {
            this.listener.Start();

            this.listener.BeginGetContext(ProcessRequest, this.listener);
        }

        public void Stop()
        {
            this.listener.Abort();
        }

        private void ProcessRequest(IAsyncResult result)
        {
            try
            {
                HttpListener listener = (HttpListener)result.AsyncState;
                HttpListenerContext context = listener.EndGetContext(result);

                string response = ProcessRequest(context.Request);

                byte[] buffer = Encoding.UTF8.GetBytes(response);

                context.Response.ContentLength64 = buffer.Length;
                System.IO.Stream output = context.Response.OutputStream;
                output.Write(buffer, 0, buffer.Length);
                output.Close();

                this.listener.BeginGetContext(ProcessRequest, this.listener);
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                log.Error(String.Format("Error starting reader {0} port {1}", this.Reader.ReaderName, this.Reader.WebPort), ex);
            }

        }

        protected string ToJson(Type type, Dto.IJsonObject jsonObject)
        {
            DataContractJsonSerializer s = new DataContractJsonSerializer(type);
            MemoryStream ms = new MemoryStream();
            s.WriteObject(ms, jsonObject);

            string json = Encoding.Default.GetString(ms.ToArray());
            ms.Close();

            return json;
        }

        protected abstract string ProcessRequest(HttpListenerRequest httpListenerRequest);
    }
}
