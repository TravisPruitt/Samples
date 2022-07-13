using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Threading;
using System.Diagnostics;
using System.Configuration;

namespace Disney.xBand.xBMS.Simulator.Listeners
{
    public abstract class Listener : IListener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private HttpListener listener;

        private MessageRepository repository;

        protected MessageRepository Repository { get { return this.repository; } }

        public Boolean IsListening { get { return this.listener.IsListening; } }
        
        public Listener(MessageRepository repository, string suffix)
        {
            if (!HttpListener.IsSupported)
            {
                throw new ApplicationException("HttpListener is not supported on this platform.");
            }

            // Create a listener.
            this.listener = new HttpListener();

            int webPort = int.Parse(ConfigurationManager.AppSettings["WebPort"]);
           
            // Add the prefixes.
            if (suffix.Length == 0)
            {
                this.listener.Prefixes.Add(String.Format("http://+:{0}/xbms-rest/app/", webPort));
            }
            else
            {
                this.listener.Prefixes.Add(String.Format("http://+:{0}/xbms-rest/app/{1}/", webPort, suffix));

            }

            this.repository = repository;
        }

        public void Start()
        {
            this.listener.Start();

            this.listener.BeginGetContext(ProcessRequest, this.listener);
        }

        public void Stop()
        {
            this.listener.Stop();
            this.listener.Close();
        }

        private void ProcessRequest(IAsyncResult result)
        {
            try
            {
                HttpListenerContext context = listener.EndGetContext(result);

                listener.BeginGetContext(ProcessRequest, null);

                string response = ProcessRequest(context);

                byte[] buffer = Encoding.UTF8.GetBytes(response);

                context.Response.ContentLength64 = buffer.Length;
                System.IO.Stream output = context.Response.OutputStream;
                output.Write(buffer, 0, buffer.Length);
                output.Close();

                context.Response.Close();

            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                Debug.WriteLine(ex.StackTrace);

                log.Error("Error processing request", ex);

            }

        }

        protected abstract string ProcessRequest(HttpListenerContext context);
    }
}
